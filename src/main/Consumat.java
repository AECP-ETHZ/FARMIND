package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.cli.*;

import logging.ABMActivityLog;
import logging.ABMTimeStepLog;
import mathematical_programming.MP_Interface;
import mathematical_programming.WeedControl;
import reader.ReadData;
import activity.Activity;
import agent.Farm;

/** 
 * This class contains the main() method of this program. 
 * Full simulation runs inside of the main() method by creating farm objects and making decisions for each farm.
 *
 */
public class Consumat {
	static long line_counter = 0;
	static int file_counter = 1;
	static String origFileName = createFileName();										           // file name for logging
	static String FileName = origFileName + String.format("%d",0);								   // given enough lines in the log file, a new file is needed.
	private static final Logger LOGGER = Logger.getLogger("FARMIND_LOGGING");
	static FileHandler fh;  
	 
	public static void main(String[] args) {
		initializeLogging();
		LOGGER.info("Starting FARMIND: version number: 0.7.0");
		CommandLine cmd = parseInput(args);														   // parse input arguments
		
		ReadData            reader             = new ReadData();							       // read all input data files
		List<Farm>          allFarms           = reader.getFarms();					               // build set of farms 
		
		List<Double> MP_Incomes = new ArrayList<Double>();		                                   // list of all farm incomes generated by the MP model
		List<ArrayList<Activity>> MP_Activities = new ArrayList<ArrayList<Activity>>();	           // list of all farm activities selected by MP model. MP returns list of activities for each farm, thus a list of lists
		double income = 0;													                       // specific income of farm 		
		ArrayList<Activity> activity = null;											           // specific activity list of the farm
		int farmIndex = 0;													                       // index of specific farm in list
		int simYear = Integer.parseInt(cmd.getOptionValue("year"));								   // number of simulation years for ABM to run
		
		initializePopulationIncomeChangeRate(allFarms);						                       // initialize the farms with the input values before starting the full ABM simulation

		for (int year = 1; year <= simYear; year++) {		                       // run simulation for a set of years, getting updated income and activities from the MP model each iteration
			LOGGER.info(String.format("Year %d simulation started", year));
			MP_Interface MP;
			
			if (cmd.getOptionValue("modelName") == "WEEDCONTROL") {
				MP = new WeedControl();
			} 
			else {
				MP = new WeedControl();
			}
			
			farmIndex = 0;
			for (Farm farm : allFarms) {
				if (year == 1) {											                       // ignore first year updated as we already have that initialized with input file
					income = -1;
				} else {
					income = MP_Incomes.get(farmIndex);										       // for all other years get the MP income and the MP activities to update each farm
					activity = MP_Activities.get(farmIndex);
				}
				
				if (farm.getStrategy() == 1) {													   // Exit from farming
					activity = MP.getExitActivity();
				}
				
				farm.updateExperience();                              			                   // each time period update experience
				farm.updateFarmParameters(allFarms, income, activity);         
				
				List<String> possibleActivitySet = farm.decideActivitySet(allFarms,cmd);      
				
				ABMTimeStepLog log = new ABMTimeStepLog(farm.getPreferences().getDataElementName(), farm.getFarmName(), year, farm.getLearningRate(), farm.getActivity_Dissimilarity(), farm.getIncome_Dissimilarity(), farm.getSatisfaction(), farm.getStrategy(), farm.getIncomeHistory().get(0), farm.getCurrentActivity(), possibleActivitySet, farm);
				updateLogFileName();
				log.appendLogFile(FileName);
				
				MP.inputsforMP(farm.getFarmName(), possibleActivitySet);
				
				farm.updateAge();                              				                       // each time period update age
				farmIndex++;                                                                       // go to next farm in list
			}
			
			MP.runModel(allFarms.size(),year);													   // if needed, update mp script and then start model
			MP_Incomes = MP.readMPIncomes();
			MP_Activities = MP.readMPActivities();
			
			farmIndex = 0;
			for (Farm farm : allFarms) {
				ABMActivityLog log = new ABMActivityLog(farm.getPreferences().getDataElementName(), farm.getFarmName(), year, farm.getStrategy(), farm.getCurrentActivity(), MP_Activities.get(farmIndex));
				log.appendLogFile(FileName);
				farmIndex++; 
			}
			
			updatePopulationIncomeChangeRate(allFarms, MP_Incomes);                                // at end of time step update the percent change for population
			
			LOGGER.info(String.format("Year %d simulation finished", year));
		}

		LOGGER.info("ABM Operation Complete.");
	}
	
	private static void initializeLogging() {
        try {
			fh = new FileHandler("ABM.log");
	        LOGGER.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	/** 
	 * Parse command line input arguments for simulation control
	 * Required arguments are:
	 * -year :: number of years of simulation
	 * -modelName :: which specific optimization model to use
	 * Optional Arguments are as follows:
	 * -uncertainty :: if set to 0, then ABM will not use the dissimilarity calculations during simulations
	 * -individual_learning :: if set to 0, then the ABM will not use individual learning for activities during simulation
	 * -social_learning :: if set to 0, then the ABM will not use social learning for activities during simulation
	 * -activity_preference :: if set to 0, then the ABM will not use activity preference during simulation
	 * @param args
	 * @return
	 */
	private static CommandLine parseInput(String[] args) {
		
		if (args.length < 1) {
			LOGGER.severe("Exiting Farmind. Input number of iterations.");
			System.exit(0);
		} 
		if (args.length < 2) {
			LOGGER.severe("Exiting Farmind. Input MP Model: WEEDCONTROL or SWISSLAND.");
			System.exit(0);
		} 
		
        Options options = new Options();

        Option yearCLI = new Option("year", true, "ABM simulation years");
        yearCLI.setRequired(true);
        options.addOption(yearCLI);
        
        Option modelName = new Option("modelName", true, "model: WEEDCONTROL or SWISSLAND");
        modelName.setRequired(true);
        options.addOption(modelName);
        
        Option uncertaintyCLI = new Option("uncertainty", true, "1 or 0 to use uncertainty in ABM");
        uncertaintyCLI.setRequired(true);
        options.addOption(uncertaintyCLI);
        
        /*Option individual_learning = new Option("individual_learning", true, "1 or 0 to use individual learning in ABM");
        individual_learning.setRequired(false);
        options.addOption(individual_learning);
        
        Option social_learning = new Option("social_learning", true, "1 or 0 to use social learning in ABM");
        social_learning.setRequired(false);
        options.addOption(social_learning);
        
        Option activity_preference = new Option("activity_preference", true, "1 or 0 to use activity preference in ABM");
        activity_preference.setRequired(false);
        options.addOption(activity_preference);
        */

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Each argument is formatted as \"-argument VALUE\"", options);
            System.exit(1);
        }
		
		return cmd;
	}
		

	/**
	 * For the output log file, we update the name after 1 million lines. Excel is not able to parse CSV files with more than 1 Million lines of data
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
	 * @param allFarms: list of all farms in region
	 */
	public static void initializePopulationIncomeChangeRate(List<Farm> allFarms) {	
		List<Double> differenceIncomeYears = new ArrayList<Double>();
		List<Double> populationYearlyMeanIncome = new ArrayList<Double>();
		
		int memory = allFarms.get(0).getMemory();                              // assume all farms have same memory length
		
		for(int i = 0; i < memory; i++) {
			List<Double> incomeFarmYear = new ArrayList<Double>();
			for (Farm farm: allFarms) {
				incomeFarmYear.add(farm.getIncomeHistory().get(i)); 
			}
			populationYearlyMeanIncome.add(mean(incomeFarmYear));	
		}
		
		int meanYearCount = 2;                                                 // number of years to count in the past for the mean calculation
		
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
	 * @param allFarms: list of all farms in region
	 * @param thisYearIncome: list of income values for all farms
	 */
	public static void updatePopulationIncomeChangeRate(List<Farm> allFarms, List<Double> thisYearIncome) {
		List<Double> differenceIncomeYears = new ArrayList<Double>();
		List<Double> populationYearlyMeanIncome = new ArrayList<Double>();
		
		int memory = allFarms.get(0).getMemory();                              // assume all farms have same memory length
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
		
		int meanYearCount = 2;                                                 // number of years to count in the past for the mean calculation
		
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
	 * @param list of values to calculate mean with
	 * @return mean: average of the input list
	 */
	private static double mean(List<Double> list) {
		double mean = 0;												       // mean value to return
		
		for (int i = 0; i<list.size(); i++) {
			mean = mean + list.get(i);
		}
		
		return mean / list.size();
	}

	/** 
	 * This function creates generic file name so that version number can be appended to end. 
	 * @return fileName
	 */
	public static String createFileName() {
		Calendar now = Calendar.getInstance();                             // Gets the current date and time
		int day = now.get(Calendar.DAY_OF_MONTH); 
		int month = now.get(Calendar.MONTH) + 1;
		int year_file = now.get(Calendar.YEAR);
		int hour = now.get(Calendar.HOUR);
		int minute = now.get(Calendar.MINUTE);	
		String fileName = String.format("Results-%d%02d%02d_%d-%d_v", year_file, month, day, hour, minute);
		
		return fileName;
	}
}
