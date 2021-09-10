package farmind.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import farmind.activity.Activity;
import farmind.agent.Farm;
import farmind.logging.ABMActivityLog;
import farmind.logging.ABMTimeStepLog;
import farmind.mathematical_programming.FactoryMP;
import farmind.mathematical_programming.MP_Interface;
import farmind.mathematical_programming.WeedControl;
import farmind.mathematical_programming.PreCalculated;
import farmind.reader.ReadData;

/** 
 * This class contains the main() method of this program. 
 * Full simulation runs inside of the main() method by creating farm objects and making decisions for each farm.
 * Each time period, update the farm and run the linear optimization model. 
 * 
 * @author kellerke
 */
public class Consumat {
    static long line_counter = 0;
    static int file_counter = 1;
    static String origFileName;  // file name for logging
    static String FileName;  // After 1 million lines, excel cannot read the file so we have to append at a certian 
    private static final Logger LOGGER = Logger.getLogger("FARMIND_LOGGING");
    static FileHandler fh;
     
    public static void main(String[] args) throws FileNotFoundException, IOException {
        initializeLogging();
        LOGGER.info("Starting FARMIND: version number: 0.12.0");
                
        Properties                cmd                 = parseInput(args,false);                    // parse input arguments from control.properties
        ReadData                  reader              = new ReadData(cmd);                         // read all input data files
        List<Farm>                allFarms            = reader.getFarms();                         // build set of farms 
        List<Double>              MP_Incomes          = new ArrayList<Double>();                   // list of all farm incomes generated by the MP model
        List<ArrayList<Activity>> MP_Activities       = new ArrayList<ArrayList<Activity>>();      // list of all farm activities selected by MP model. MP returns list of activities for each farm, thus a list of lists
        List<Double>              MP_Impacts;                                                         // list of all farm thgs generated by the MP model
        int                       simYear             = Integer.parseInt(cmd.getProperty("year")); // number of simulation years for ABM to run
        int                       memoryLengthAverage = averageMemoryLength(allFarms);             // average memory length of all agents/farms
        boolean                   pricingAverage      = true;                                      // use the average pricing information for the historical memory length
        
        if (cmd.getProperty("orig_file_name") != null) {
            origFileName = cmd.getProperty("orig_file_name");
            FileName = String.format("%s_%d",origFileName, 0);
            for (String ending : Arrays.asList("parameters", "activity_averagePrice", "activity_actualPrice")) {
                File csv = new File(String.format("./output/%s_%s.csv", FileName, ending));
                if (csv.exists()) csv.delete();
            }
        } else {
            origFileName = createFileName();  // file name for logging
            FileName = origFileName + String.format("%d",0);
        }
        
        // initialize the farms with the input values before starting the full ABM simulation
        initializePopulationIncomeChangeRate(allFarms);

        for (int year = 1; year <= simYear; year++) {
            LOGGER.info(String.format("Year %d simulation started", year));
            
            MP_Interface MP = new FactoryMP(cmd, simYear, memoryLengthAverage).getMP();            // select correct model based on input command parameter
            
            allFarmsDecideActivity(cmd, year, allFarms, MP, MP_Incomes, MP_Activities);            // generate model files based on decisions made
            pricingAverage = !(MP instanceof PreCalculated);                                                                 // use average of historical price
            MP.runModel(cmd, allFarms.size(), year, pricingAverage, memoryLengthAverage);          // update gams script and then start model (ie update pricing information etc)
            MP_Incomes = MP.readMPIncomes(cmd, allFarms);                                          // read income and activities from model results
            MP_Impacts = MP.readMPImpacts(cmd, allFarms);
            MP_Activities = MP.readMPActivities(cmd, allFarms);
            logActivitesMP(cmd, year, pricingAverage, allFarms, MP_Incomes, MP_Impacts, MP_Activities, MP);
                    
            // rerun Weedcontrol model using selected strategy, but actual pricing information
            if (MP instanceof WeedControl) {
                runModelWeedSingle(MP, allFarms, MP_Activities);
                pricingAverage = false;                                                            // use actual pricing information
                MP.runModel(cmd, allFarms.size(),year,pricingAverage,memoryLengthAverage);         // if needed, update mp script and then start model
                MP_Incomes = MP.readMPIncomes(cmd, allFarms);
                MP_Impacts = MP.readMPImpacts(cmd, allFarms);
                MP_Activities = MP.readMPActivities(cmd, allFarms);
                logActivitesMP(cmd, year, pricingAverage, allFarms, MP_Incomes, MP_Impacts, MP_Activities, MP);
            }
            
            updatePopulationIncomeChangeRate(allFarms, MP_Incomes);                                // at end of time step update the percent change for population
            
            LOGGER.info(String.format("Year %d simulation finished", year));
        }

        LOGGER.info("ABM Operation Complete.");
    }
    
    /** 
     * rerun Weedcontrol model using selected strategy, but actual pricing information
     * this needs to be done twice as the first time gets the activity based on EXPECTED income
     * the second time we use the selected activity(ies) and get the ACTUAL income of that agent for the selected strategy
     * @param MP :: Mathematical programming object to manage model
     * @param allFarms :: list of all farms in system
     * @param MP_Activities :: List of activities for each farm
     * @throws IOException 
     */
    private static void runModelWeedSingle(MP_Interface MP, List<Farm> allFarms, List<ArrayList<Activity>> MP_Activities) throws IOException {
        int farmIndex = 0;
        for (Farm farm : allFarms) {

            List<Activity> selectedActivity = MP_Activities.get(farmIndex); 
            List<String>   selectedActivityString = new ArrayList<>();  // create string list of final selected activities
            
            for (Activity act: selectedActivity) {
                selectedActivityString.add(act.getName());
            }
            
            MP.inputsforMP(farm, selectedActivityString);
            farmIndex++;
        }
    }
    
    /** 
     * For each farm in allFarms list, update the individual farm and then make the decision about best activity based on satisfaction and dissimilarity and prepare MP model control files
     * @param cmd :: input control properties for simulation
     * @param year :: current iteration of model
     * @param allFarms :: list of all farm agents
     * @param MP :: Mathematical programming (gams) interface object
     * @param MP_Incomes :: list of incomes from previous time period
     * @param MP_Activities :: list of activities from previous time period
     * @throws IOException 
     */
    private static void allFarmsDecideActivity(Properties cmd, int year, List<Farm> allFarms, MP_Interface MP, 
            List<Double> MP_Incomes, List<ArrayList<Activity>> MP_Activities) throws IOException {
        int farmIndex = 0;
        double income = 0;    
        ArrayList<Activity> activity = null;
        for (Farm farm : allFarms) {
            if (year == 1) {                                                                       // ignore first year updated as we already have that initialized with input file
                income = -1;
            } else {
                income = MP_Incomes.get(farmIndex);                                                // for all other years get the MP income and the MP activities to update each farm
                activity = MP_Activities.get(farmIndex);
            }
            
            if (farm.getStrategy() == 1) {                                                         // If farm exited farm activities previously, then keep farm as exit activity
                activity = MP.getExitActivity();
            }
            
            farm.updateExperience();                                                               // each time period update experience
            farm.updateFarmParameters(allFarms, income, activity);
            List<String> possibleActivitySet = farm.decideActivitySet(allFarms, cmd);
            
            ABMTimeStepLog log = new ABMTimeStepLog(
                    cmd.getProperty("modelName"),
                    year,
                    possibleActivitySet,
                    farm);
            updateLogFileName();
            log.appendLogFile(FileName);
            
            MP.inputsforMP(farm, possibleActivitySet);
        
            farm.updateAge();                                                                      // each time period update age
            farmIndex++;                                                                           // go to next farm in list
        }
    }

    /** 
     * Create/update log file after MP run model results are read
     * @param cmd :: input control properties for simulation
     * @param year :: model iteration
     * @param pricingAverage :: indicate if average historical crop price, or actual crop price was used for model run
     * @param allFarms :: list of all farm agents
     * @param MP_Incomes :: list of incomes from previous time period
     * @param MP_Impacts 
     * @param MP_Activities :: list of activities from previous time period
     * @param MP_Interface :: mathematical programming model. Used to set the exit activity in the case where it is required. Makes the log cleaner. 
     * @throws IOException 
     */
    private static void logActivitesMP(Properties cmd, int year, boolean pricingAverage, List<Farm> allFarms, List<Double> MP_Incomes, List<Double> MP_Impacts, List<ArrayList<Activity>> MP_Activities, MP_Interface MP) throws IOException {
        int farmIndex = 0;
        for (Farm farm : allFarms) {
            ArrayList<Activity> activity = MP_Activities.get(farmIndex);
            if (farm.getStrategy() == 1) {
                activity = MP.getExitActivity();
            }
            
            ABMActivityLog log = new ABMActivityLog(
                    cmd.getProperty("modelName"),
                    year,
                    activity,
                    MP_Incomes.get(farmIndex),
                    MP_Impacts.get(farmIndex),
                    farm);
            log.appendLogFile(FileName,pricingAverage);
            farmIndex++; 
        }
    }

    /** 
     * Setup logging for system to allow easier debugging of issues caused by runtime decisions
     */
    private static void initializeLogging() {
        try {
            fh = new FileHandler("ABM.log");
            
            fh.setLevel(Level.ALL);
            
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            
            LOGGER.setLevel(Level.ALL);
            
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  
    }
    
    /**
     * Calculate average memory length for all agents in system. 
     * This is used to set an average crop price during simulation years. 
     * @param allFarms ::      list of all farms in system
     * @return memoryLength :: length of average memory in system for all farms
     */
    private static int averageMemoryLength(List<Farm> allFarms) {
        int memoryLength = 0;
        
        for (Farm farm:allFarms) {
            memoryLength += farm.getMemory();
        }
        
        memoryLength = memoryLength / allFarms.size();
        return memoryLength;
    }
    
    /** 
     * Parse command line input arguments for simulation control 
     * -year :: number of years of simulation
     * @param args :: command line arguments to control the system. In this case only 1 argument
     * @param test :: parse the test data or the actual control.properties file
     * @return prop :: return the parsed properties file as an object we can use later in the model
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public static Properties parseInput(String[] args, boolean test) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        String file = test
                    ? "test_data/control.properties"
                    : "control.properties";
        
        try (FileReader input = new FileReader(file)) {

            //load a properties file from class path
            prop.load(input);
            //overwrite some properties
            if (args.length >= 1) {
                prop.setProperty("year", args[0]);
                LOGGER.info("set iteration count to " + prop.getProperty("year"));
            }
            if (args.length >= 2) {
                prop.setProperty("orig_file_name", args[1]);
                LOGGER.info("write output to " + prop.getProperty("orig_file_name") +"...");
            }
 
            return prop;
        }
    }
        
    /**
     * For the output log file, we update the name after 1 million lines. 
     * Since Excel is not able to parse CSV files with more than 1 Million lines of data we need to add to the file name
     */
    private static void updateLogFileName() {
        line_counter++;
        if (line_counter > 1000000) {
            FileName = origFileName + String.format("%d", file_counter);
            file_counter++;
            line_counter = 0;
        } 
    }
    
    /** 
     * This function initializes the income growth rate of the population (in a region) for all farms.
     * @param allFarms :: list of all farms in region
     */
    public static void initializePopulationIncomeChangeRate(List<Farm> allFarms) {    
        List<Double> differenceIncomeYears = new ArrayList<Double>();
        List<Double> populationYearlyMeanIncome = new ArrayList<Double>();
        
        int memory = allFarms.get(0).getMemory();  // assume all farms have same memory length
        
        for(int i = 0; i < memory; i++) {
            List<Double> incomeFarmYear = new ArrayList<Double>();
            for (Farm farm: allFarms) {
                incomeFarmYear.add(farm.getIncomeHistory().get(i));
            }
            populationYearlyMeanIncome.add(mean(incomeFarmYear));
        }
        
        int meanYearCount = 2;  // number of years to count in the past for the mean calculation
        
        for(int i = meanYearCount; i > 0; i-- ) {
            double diff = (populationYearlyMeanIncome.get(i-1) -  populationYearlyMeanIncome.get(i)) /  populationYearlyMeanIncome.get(i);
            differenceIncomeYears.add( diff );   
        }
        
        for (Farm farm: allFarms) {
            double changeRate = mean(differenceIncomeYears);
            farm.setAveragePopulationIncomeChangeRate(changeRate);
        }
    }
    
    /** 
     * This function updates the income growth rates based on new income for farms.
     * @param allFarms :: list of all farms in region
     * @param thisYearIncome :: list of income values for all farms
     */
    public static void updatePopulationIncomeChangeRate(List<Farm> allFarms, List<Double> thisYearIncome) {
        List<Double> differenceIncomeYears = new ArrayList<Double>();
        List<Double> populationYearlyMeanIncome = new ArrayList<Double>();
        
        int memory = allFarms.get(0).getMemory();  // assume all farms have same memory length
        double currentYearAverageIncome = mean(thisYearIncome);
        populationYearlyMeanIncome.add(currentYearAverageIncome);
        
        // get the average income for the population for each year, but skip the oldest income. 
        // The incomes will get updated for each agent, and the oldest income will be removed. 
        for(int i = 0; i < memory-1; i++) {
            List<Double> incomeFarmYear = new ArrayList<Double>();
            for (Farm farm: allFarms) {
                incomeFarmYear.add(farm.getIncomeHistory().get(i)); 
            }
            populationYearlyMeanIncome.add(mean(incomeFarmYear));    
        }
        
        int meanYearCount = 2;  // number of years to count in the past for the mean calculation
        
        for(int i = meanYearCount; i > 0; i-- ) {
            double diff = (populationYearlyMeanIncome.get(i-1) -  populationYearlyMeanIncome.get(i)) /  populationYearlyMeanIncome.get(i);
            differenceIncomeYears.add( diff );   
        }
        
        for (Farm farm: allFarms) {
            double changeRate = mean(differenceIncomeYears);
            farm.setAveragePopulationIncomeChangeRate(changeRate);
        }
    }
    
    /** 
     * This function calculates the mean of provided list 
     * @param list :: values for mean calculation
     * @return mean :: average of the input list
     */
    private static double mean(List<Double> list) {
        double mean = 0;  // mean value to return
        
        for (int i = 0; i<list.size(); i++) {
            mean = mean + list.get(i);
        }
        
        return mean / list.size();
    }

    /** 
     * This function creates generic file name so that version number can be appended to end. 
     * @return fileName :: base name of output logging file
     */
    public static String createFileName() {
        Calendar now = Calendar.getInstance();  // Gets the current date and time
        int day = now.get(Calendar.DAY_OF_MONTH); 
        int month = now.get(Calendar.MONTH) + 1;
        int year_file = now.get(Calendar.YEAR);
        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);
        String fileName = String.format("Results-%d%02d%02d_%d-%d_v", year_file, month, day, hour, minute);
        
        return fileName;
    }
}
