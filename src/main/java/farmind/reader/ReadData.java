package reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import activity.Activity;
import agent.Farm;
import agent.Location;
import agent.Person;                 

/** 
 * This class reads input parameters from configuration files and results data from the optimization model.
 * 
 * @author kellerke
 */
public class ReadData {

    public static final int NAME = 0;                                          // name of agent
    public static final int COORDINATE1 = 1;                                   // location coordinate one
    public static final int COORDINATE2 = 2;                                   // location coordinate two
    public static final int AGE = 3;                                           // Agent age
    public static final int EDUCATION = 4;                                     // Agent education level
    public static final int MEMORY = 5;                                        // Agent memory length
    public static final int BETA_L = 6;                                        // learning multiplier for fuzzy logic
    public static final int BETA_S = 7;                                        // social learning multiplier for fuzzy logic
    public static final int BETA_P = 8;                                        // preference multiplier for fuzzy logic
    public static final int REFERENCE_INCOME = 9;                              // set reference income for agent
    public static final int ASPIRATION = 10;
    public static final int INCOME_TOLERANCE = 11;                             // set income change dissimilarity tolerance
    public static final int ACTIVITY_TOLERANCE = 12;                           // set dissimilarity in activity tolerance
    public static final int LAMBDA = 13;                                       // parameter in the formula for calculating satisfaction
    public static final int ALPHA_PLUS = 14;                                   // parameter in the formula for calculating satisfaction
    public static final int ALPHA_MINUS = 15;                                  // parameter in the formula for calculating satisfaction
    public static final int PHI_PLUS = 16;                                     // parameter in the formula for calculating satisfaction
    public static final int PHI_MINUS = 17;                                    // parameter in the formula for calculating satisfaction
    public static final int OPT_FUZZY_SIZE = 18;                               // size of optimization fuzzy logic
    public static final int IMT_FUZZY_SIZE = 19;                               // size of imitation fuzzy logic
    public static final int RANKING_VERSION = 20;                              // size of imitation fuzzy logic
    public static final int LEARNING_RATE = 21;                                // learning rate for external input 
    
    public String FarmParametersFile;                                          // allow external function to set data files for testing
    public String ActivityPreferenceFile;
    public String InitialActivities;
    public String InitialIncomes;
    public String InitialPerformingYears;
    public String SocialNetworkFile;
    
    public int ThisYear;
    
    private static final Logger LOGGER = Logger.getLogger("FARMIND_LOGGING");
    
    public ReadData(Properties cmd) {
        this.FarmParametersFile = String.format("./%s/farm_parameters.csv",cmd.getProperty("data_folder"));                       // allow external function to set data files for testing
        this.ActivityPreferenceFile = String.format("./%s/activity_preference.csv",cmd.getProperty("data_folder"));
        this.InitialActivities = String.format("./%s/initial_activities.csv",cmd.getProperty("data_folder"));
        this.InitialIncomes = String.format("./%s/initial_incomes.csv",cmd.getProperty("data_folder"));
        this.InitialPerformingYears = String.format("./%s/initial_performing_years.csv",cmd.getProperty("data_folder"));
        this.SocialNetworkFile = String.format("./%s/social_networks.csv",cmd.getProperty("data_folder"));
        
        String year = cmd.getProperty("start_year_simulation");
        this.ThisYear = year == null
                      ? Calendar.getInstance().get(Calendar.YEAR)
                      : Integer.parseInt(year);
    }

    /**
     * Each farm in the list contains a social network, the associated people, and preferred activities
     * The satisfaction and Information Seeking Behavior (ISB) are generated initially
     * @return List of all farm objects from the input csv file
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public List<Farm> getFarms() throws FileNotFoundException, IOException {
        String Line;
        List<Farm> farms = new ArrayList<Farm>();
        ArrayList<String> farmParameters;
        int farm_count_index = 0;                                              // index is used to set the actual farm id value
        
        String name = "";
        int age = 0;
        int education = 0;
        int memory = 0;
        double beta_l = 0;
        double beta_s = 0;
        double beta_p = 0;
        double reference_income = 0;
        double aspiration_coef = 0;
        double activity_tolerance = 0;
        double income_tolerance = 0;
        double lambda = 0;
        double alpha_plus = 0;
        double alpha_minus = 0;
        double phi_plus = 0;
        double phi_minus = 0;
        double opt_fuzzy_size = 0;
        double imt_fuzzy_size = 0;
        int ranking_version = 0;
        double learning_rate = 0;
    
        List<Graph<String, DefaultEdge>> network = this.getSocialNetworks();   
        List<Activity>                   activities = getActivityList();
        FarmDataMatrix                   preference = getPreferences();
        FarmDataMatrix                   experience = getExperience();
        
        if( experience.sizeFarms() != preference.sizeFarms() ) {
            LOGGER.severe("Exiting Farmind. Experience and preference list do not match in size. Please confirm input data.");
            System.exit(0);
        }
        
        if( activities.size() != preference.sizeData() ) {
            LOGGER.severe("Exiting Farmind. Preference file and activity list do not match. Please confirm input data.");
            System.exit(0);
        }
        
        if( network.size() != preference.sizeFarms() ) {
            LOGGER.severe("Exiting Farmind. Social network size and preferences do not match. Please confirm input data.");
            System.exit(0);
        }
        
        // Read data files and create list of farms
        try (BufferedReader Buffer = new BufferedReader(new FileReader(this.FarmParametersFile))) {
             
            
            Line = Buffer.readLine();                                           // first line with titles to throw away
            
            while ((Line = Buffer.readLine()) != null) {                       
                farmParameters = CSVtoArrayList(Line);                           // Read farm's parameters line by line
                
                if( farmParameters.size() != (LEARNING_RATE+1) ) {
                    LOGGER.severe("Exiting Farmind. Input Farm Parameter file smaller than expected.");
                    System.exit(0);
                }
                
                Location location = new Location();                               // create new location for each farm
                List<Activity> currentActivity = new ArrayList<Activity>();    // dummy variable to create initial farm object. Initialized after creation. 
                List<Double> income = new ArrayList<Double>();                   // dummy variable to create initial farm object. Initialized after creation. 
                double[] coordinates = {0,0};                                   // location of farm
                
                name = farmParameters.get(NAME);
                if (name.contains("\"")) {
                        LOGGER.severe("Exiting Farmind. Please do not use quotation marks around agent names or anywhere in files");
                        System.exit(0);
                }
                coordinates[0] = Double.parseDouble(farmParameters.get(COORDINATE1));
                coordinates[1] = Double.parseDouble(farmParameters.get(COORDINATE2));
                location.setCoordinates(coordinates);
                
                age = this.ThisYear - Integer.parseInt( farmParameters.get(AGE));
                education = Integer.parseInt( farmParameters.get(EDUCATION) );
                memory = Integer.parseInt( farmParameters.get(MEMORY));
                if (memory < 4) {
                    memory = 4;                                                // error in calculations if memory is less than 4
                    LOGGER.severe("Exiting Farmind. Memory length needs to be greater than 4.");
                    System.exit(0);
                }
                
                beta_l = Double.parseDouble( farmParameters.get(BETA_L) );
                beta_s = Double.parseDouble( farmParameters.get(BETA_S) );
                beta_p = Double.parseDouble( farmParameters.get(BETA_P) );
                reference_income = Double.parseDouble( farmParameters.get(REFERENCE_INCOME) );
                
                aspiration_coef = Double.parseDouble( farmParameters.get(ASPIRATION));
                
                activity_tolerance = Double.parseDouble( farmParameters.get(ACTIVITY_TOLERANCE));
                income_tolerance = Double.parseDouble( farmParameters.get(INCOME_TOLERANCE));
                lambda = Double.parseDouble( farmParameters.get(LAMBDA));
                alpha_plus = Double.parseDouble( farmParameters.get(ALPHA_PLUS));
                alpha_minus = Double.parseDouble( farmParameters.get(ALPHA_MINUS));
                phi_plus = Double.parseDouble( farmParameters.get(PHI_PLUS));
                phi_minus = Double.parseDouble( farmParameters.get(PHI_MINUS));
                opt_fuzzy_size = Double.parseDouble(   farmParameters.get(OPT_FUZZY_SIZE) );
                imt_fuzzy_size = Double.parseDouble(   farmParameters.get(IMT_FUZZY_SIZE) );
                ranking_version = Integer.parseInt(  farmParameters.get(RANKING_VERSION) );
                learning_rate = Double.parseDouble(  farmParameters.get(LEARNING_RATE));

                Person farmHead = new Person(age, education, memory);
                Farm farm = new Farm(name, location, network.get(farm_count_index),
                        income, experience, preference, activities,
                        activity_tolerance, income_tolerance, currentActivity, farmHead,
                        beta_l, beta_s, beta_p, reference_income, aspiration_coef, lambda, alpha_plus, alpha_minus, phi_plus, phi_minus, opt_fuzzy_size, imt_fuzzy_size, ranking_version, learning_rate);
                
                farms.add(farm);
                farm_count_index++;
            }
        }
        
        boolean success = false;
        success = initializeFarmActivities(farms);                             // read initialization activity data from data file and set all initialize all activities
        if (success == false) {
            LOGGER.severe("Error setting initial farm activities");
        }                                   
        
        success = initializeFarmIncomes(farms);                                // read initialization income data from data file and set all initialize all incomes
        if (success == false) {
            LOGGER.severe("Error setting initial farm activities");
        }
        
        // check the learning rate for all the farms. If the learning rate is 0 (ie externally set from parameter to 0), then calculate a rate based on memory length
        for (Farm farm : farms) {
            if (farm.getLearningRate() == 0)
                farm.calculate_setLearningRate();
        }
        
        return farms;
    }
    
    /** 
     * initialize farm activity by reading activity file
     * @param farms :: List of farms in system
     * @return success :: if the farms have been updated correctly 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private boolean initializeFarmActivities(List<Farm> farms) throws FileNotFoundException, IOException {
        boolean success = false;
        String Line;
        ArrayList<String> farmParameters;
        int farm_count_index = 0;                                              // index is used to set the actual farm id value
        List<Activity>                   activities = getActivityList();
        
        try (BufferedReader Buffer = new BufferedReader(new FileReader(this.InitialActivities))) {
            Line = Buffer.readLine();                                           // first line with titles to throw away
            farm_count_index = 0;    
            while ((Line = Buffer.readLine()) != null) { 
                farmParameters = CSVtoArrayList(Line);                           // Read farm's parameters line by line
                List<Activity> currentActivity = new ArrayList<Activity>();    // each farm has list of activities

                currentActivity.clear();
                for (int k = 0; k < farmParameters.size(); k++) {
                    for(int i = 0; i<activities.size(); i++) {
                        if (activities.get(i).getName().equals(farmParameters.get(k) )) {
                            int ID = activities.get(i).getID();
                            Activity p = new Activity(ID, farmParameters.get(k)); 
                            currentActivity.add(p);
                        }
                    }
                }                
                farms.get(farm_count_index).setCurrentActivity(currentActivity);
                farm_count_index++;    
            }
            
            success = true;
        }
        
        return success;
    }
    
    /** 
     * Read the initial income data file and update each farm with the proper income
     * @param farms :: List of farms in system
     * @return success :: success if the farms have been updated correctly
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private boolean initializeFarmIncomes(List<Farm> farms) throws FileNotFoundException, IOException {
        boolean success = false;
        String Line;
        ArrayList<String> farmParameters;
        int farm_count_index = 0;                                              // index is used to set the actual farm id value
        
        try (BufferedReader Buffer = new BufferedReader(new FileReader(this.InitialIncomes))) {
            
            Line = Buffer.readLine();                                           // first line with titles to throw away
            farm_count_index = 0;    
            while ((Line = Buffer.readLine()) != null) { 
                farmParameters = CSVtoArrayList(Line);                           // Read farm's parameters line by line
                List<Double> income = new ArrayList<Double>();                   // each farm has income history records
                
                for (int i = 1; i < farms.get(farm_count_index).getMemory()+1; i++) {
                    income.add( Double.parseDouble( farmParameters.get(i) ) );
                }
                
                farms.get(farm_count_index).setIncomeHistory(income);
                //farms.get(farm_count_index).setLearningRate();
                farms.get(farm_count_index).updateAveragePersonalIncomeChangeRate();
                farm_count_index++;    
            }
            
            success = true;
        }
        
        return success; 
    }
    
    /**
     * Read preferences of each farm for each activity and build preference object
     * @return matrix of the farm region preferences
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private FarmDataMatrix getPreferences() throws FileNotFoundException, IOException {
        String Line;
        ArrayList<String> matrixRow;
        FarmDataMatrix preferences = new FarmDataMatrix();

        try (BufferedReader Buffer = new BufferedReader(new FileReader(this.ActivityPreferenceFile))) {
            Line = Buffer.readLine();
            matrixRow = CSVtoArrayList(Line);
            matrixRow.remove(0);
            preferences.setDataElementName(matrixRow);
            
            while ((Line = Buffer.readLine()) != null) {                       // Read row data
                matrixRow = CSVtoArrayList(Line);
                preferences.setFarmMap(matrixRow);
            }
        }
        
        return preferences;
    }
    
    /** 
     * read years of experience file 
     * @return object corresponding to years performing activity for each farm
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private FarmDataMatrix getExperience() throws FileNotFoundException, IOException {
        String Line;
        ArrayList<String> matrixRow;
        FarmDataMatrix experience = new FarmDataMatrix();

        try (BufferedReader Buffer = new BufferedReader(new FileReader(this.InitialPerformingYears))) {
            Line = Buffer.readLine();
            matrixRow = CSVtoArrayList(Line);
            matrixRow.remove(0);
            experience.setDataElementName(matrixRow);
            
            while ((Line = Buffer.readLine()) != null) {                       // Read row data
                matrixRow = CSVtoArrayList(Line);
                experience.setFarmMap(matrixRow);
            }
        }
        
        return experience;
    }
    
    /**
     * Read a CSV file that specifies each farm and generate a star network for each listed farm
     * Each farm id/name is set as the root of the star graph, and each associated node has an associated link weight
     * Each farm will have an individual graph set based on the master list produced in this method
     * @return List of graphs for each farm
     * @throws IOException 
     */
    private List<Graph<String, DefaultEdge>> getSocialNetworks() throws IOException {
        List<Graph<String, DefaultEdge>> NetworkList = new ArrayList<Graph<String, DefaultEdge>>();
        String Line;
        ArrayList<String> data;
        ArrayList<String> FarmNames;
        DefaultEdge edge;
        
        try (BufferedReader Buffer = new BufferedReader(new FileReader(this.SocialNetworkFile))){
            
            Line = Buffer.readLine();
            FarmNames = CSVtoArrayList(Line);
            FarmNames.remove(0);
            
            while ((Line = Buffer.readLine()) != null) {
                data = CSVtoArrayList(Line);
                Graph<String, DefaultEdge> g = new SimpleWeightedGraph<String, DefaultEdge>(DefaultEdge.class);
    
                // build graph with all nodes
                for (int i = 0; i<FarmNames.size(); i++)
                {
                    g.addVertex(FarmNames.get(i));
                }
                
                // add all nodes except root to graph as vertices
                for (int i = 0; i<FarmNames.size(); i++)
                {
                    if (data.get(0).equalsIgnoreCase(FarmNames.get(i)))
                    {
                        continue;
                    }
                    edge = g.addEdge( data.get(0), FarmNames.get(i) );
                    g.setEdgeWeight(edge, Double.parseDouble(data.get(i+1)) );
                }
                NetworkList.add(g);
            }
        }
        
        return NetworkList;
    }

    /**
     * Create list of activity type/category by reading the years of experience file
     * This is used to generate the individual farm activities lists
     * @return List of activities in the master CSV file
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public List<Activity> getActivityList() throws FileNotFoundException, IOException {
        String Line;
        List<Activity> activities = new ArrayList<Activity>();
        ArrayList<String> activityRow;
        
        try (BufferedReader Buffer = new BufferedReader(new FileReader(this.InitialPerformingYears))) {
            Line = Buffer.readLine();                                           // first line to be deleted
            activityRow = CSVtoArrayList(Line);
            int ID = 100;                                                       // initial ID value of 100
            activityRow.remove(0);
            for (String act: activityRow) {
                ID = ID + 100;
                
                Activity activity = new Activity(ID, act);
                activities.add(activity);
            }
        }
        
        return activities;
    }
    
    /**
     * This function converts data from CSV file into array structure 
     * @param CSV :: String from input CSv file to break into array
     * @return Result :: ArrayList of strings 
     */
    private static ArrayList<String> CSVtoArrayList(String CSV) {
        ArrayList<String> Result = new ArrayList<String>();
        
        if (CSV != null) {
            String[] splitData = CSV.split("\\s*,\\s*");
            for (int i = 0; i < splitData.length; i++) {
                if (!(splitData[i] == null) || !(splitData[i].length() == 0)) {
                    Result.add(splitData[i].trim());
                }
            }
        }
        return Result;
    }
}
