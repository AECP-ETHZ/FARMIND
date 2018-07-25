package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
 */
public class ReadData {

	public static final int NAME = 0;										   
	public static final int COORDINATE1 = 1;
	public static final int COORDINATE2 = 2;
	public static final int AGE = 3;										   // Agent age
	public static final int EDUCATION = 4;									   // Agent education level
	public static final int MEMORY = 5;										   // Agent memory length
	public static final int BETA_L = 6;										   // learning multiplier for fuzzy logic
	public static final int BETA_S = 7;									       // social learning multiplier for fuzzy logic
	public static final int BETA_P = 8;										   // preference multiplier for fuzzy logic
	public static final int ASPIRATION_COEF = 9;							   // set aspiration level
	public static final int INCOME_TOLERANCE = 10;							   // set income change dissimilarity tolerance
	public static final int ACTIVITY_TOLERANCE = 11;						   // set dissimilarity in activity tolerance
	public static final int LAMBDA = 12;									   // parameter in the formula for calculating satisfaction
	public static final int ALPHA_PLUS = 13;								   // parameter in the formula for calculating satisfaction
	public static final int ALPHA_MINUS = 14;								   // parameter in the formula for calculating satisfaction
	public static final int PHI_PLUS = 15;								       // parameter in the formula for calculating satisfaction
	public static final int PHI_MINUS = 16;  								   // parameter in the formula for calculating satisfaction

	public String FarmParametersFile = "./data/farm_parameters.csv";					   // allow external function to set data files for testing
	public String ActivityPreferenceFile = "./data/activity_preference.csv";
	public String InitialActivities = "./data/initial_activities.csv";
	public String InitialIncomes = "./data/initial_incomes.csv";
	public String PerformingYearsFile = "./data/performing_years.csv";
	public String SocialNetworkFile = "./data/social_networks.csv";
	
	/**
	 * Each farm in the list contains a social network, the associated people, and preferred activities
	 * The satisfaction and Information Seeking Behavior (ISB) are generated initially
	 * @return List of all farm objects from the input csv file
	 */
	public List<Farm> getFarms() {
		String Line;
		List<Farm> farms = new ArrayList<Farm>();
		ArrayList<String> farmParameters;
		BufferedReader Buffer = null;	 									   // read input file
		int farm_count_index = 0;                                              // index is used to set the actual farm id value
		
		String name = "";
		int age = 0;
		int education = 0;
		int memory = 0;
		double beta_l = 0;
		double beta_s = 0;
		double beta_p = 0;
		double aspiration_coef = 0;
		double activity_tolerance = 0;
		double income_tolerance = 0;
		double lambda = 0;
		double alpha_plus = 0;
		double alpha_minus = 0;
		double phi_plus = 0;
		double phi_minus = 0;		
	
		List<Graph<String, DefaultEdge>> network = this.getSocialNetworks();   
		List<Activity>                   activities = getActivityList();
		FarmDataMatrix                   preference = getPreferences();
		FarmDataMatrix                   experience = getExperience();
		
		// Read data files and create list of farms
		try {
			Calendar now = Calendar.getInstance();                             // Gets the current date and time
			int currentYear = now.get(Calendar.YEAR); 
			Buffer = new BufferedReader(new FileReader(FarmParametersFile));
			Line = Buffer.readLine();									       // first line with titles to throw away
			
			while ((Line = Buffer.readLine()) != null) {                       
				farmParameters = CSVtoArrayList(Line);						   // Read farm's parameters line by line
				
				Location location = new Location();							   // create new location for each farm
				List<Activity> currentActivity = new ArrayList<Activity>();    // dummy variable to create initial farm object. Initialized after creation. 
				List<Double> income = new ArrayList<Double>();				   // dummy variable to create initial farm object. Initialized after creation. 
				double[] coordinates = {0,0};								   // location of farm
				
				name = farmParameters.get(NAME);
				coordinates[0] = Double.parseDouble(farmParameters.get(COORDINATE1));
				coordinates[1] = Double.parseDouble(farmParameters.get(COORDINATE2));
				location.setCoordinates(coordinates);
				
				age = currentYear - Integer.parseInt( farmParameters.get(AGE));
				education = Integer.parseInt( farmParameters.get(EDUCATION) );
				memory = Integer.parseInt( farmParameters.get(MEMORY));
				if (memory < 4) {
					memory = 4; 											   // error in calculations if memory is less than 4
					System.out.println("Memory length needs to be greater than 4");
					System.exit(0);
				}
				
				beta_l = Double.parseDouble( farmParameters.get(BETA_L) );
				beta_s = Double.parseDouble( farmParameters.get(BETA_S) );
				beta_p = Double.parseDouble( farmParameters.get(BETA_P) );
				
				aspiration_coef = Double.parseDouble( farmParameters.get(ASPIRATION_COEF));
				activity_tolerance = Double.parseDouble( farmParameters.get(ACTIVITY_TOLERANCE));
				income_tolerance = Double.parseDouble( farmParameters.get(INCOME_TOLERANCE));
				lambda = Double.parseDouble( farmParameters.get(LAMBDA));
				alpha_plus = Double.parseDouble( farmParameters.get(ALPHA_PLUS));
				alpha_minus = Double.parseDouble( farmParameters.get(ALPHA_MINUS));
				phi_plus = Double.parseDouble( farmParameters.get(PHI_PLUS));
				phi_minus = Double.parseDouble( farmParameters.get(PHI_MINUS));

				Person farmHead = new Person(age, education, memory);        
				Farm farm = new Farm(name, location, network.get(farm_count_index), 
						income, experience, preference, activities, 
						activity_tolerance, income_tolerance, currentActivity, farmHead, 
						beta_l, beta_s, beta_p, aspiration_coef, lambda, alpha_plus, alpha_minus, phi_plus, phi_minus);
				
				farms.add(farm);
				farm_count_index++;	
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (Buffer != null) Buffer.close();
			} catch (IOException Exception) {
				Exception.printStackTrace();
			}
		}
		
		initializeFarmActivities(farms);                                       // read initialization activity data from data file and set all initialize all activities
		initializeFarmIncomes(farms);                                          // read initialization income data from data file and set all initialize all incomes
		
		return farms;
	}
	
	/** 
	 * initialize farm activity by reading activity file
	 * @param farms List of farms in system
	 */
	private void initializeFarmActivities(List<Farm> farms) {
		String Line;
		ArrayList<String> farmParameters;
		BufferedReader Buffer = null;	 									   // read input file
		int farm_count_index = 0;                                              // index is used to set the actual farm id value
		List<Activity>                   activities = getActivityList();
		
		try {
			Buffer = new BufferedReader(new FileReader(InitialActivities));
			Line = Buffer.readLine();									       // first line with titles to throw away
			farm_count_index = 0;	
			while ((Line = Buffer.readLine()) != null) { 
				farmParameters = CSVtoArrayList(Line);						   // Read farm's parameters line by line
				List<Activity> currentActivity = new ArrayList<Activity>();  // each farm has list of activities

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
				farms.get(farm_count_index).setCurrentActivity(currentActivity);;
				farm_count_index++;	
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (Buffer != null) Buffer.close();
			} catch (IOException Exception) {
				Exception.printStackTrace();
			}
		}
		
	}
	
	/** 
	 * Read the initial income data file and update each farm with the proper income
	 * @param farms List of farms in system
	 * @return farms List of farms in system
	 */
	private void initializeFarmIncomes(List<Farm> farms) {
		String Line;
		ArrayList<String> farmParameters;
		BufferedReader Buffer = null;	 									   // read input file
		int farm_count_index = 0;                                              // index is used to set the actual farm id value
		
		try {
			Buffer = new BufferedReader(new FileReader(InitialIncomes));
			Line = Buffer.readLine();									       // first line with titles to throw away
			farm_count_index = 0;	
			while ((Line = Buffer.readLine()) != null) { 
				farmParameters = CSVtoArrayList(Line);						   // Read farm's parameters line by line
				List<Double> income = new ArrayList<Double>();				   // each farm has income history records
				
				for (int i = 1; i < farms.get(farm_count_index).getMemory()+1; i++) {
					income.add( Double.parseDouble( farmParameters.get(i) ) );
				}
				
				farms.get(farm_count_index).setIncomeHistory(income);
				farms.get(farm_count_index).setLearningRate();
				farms.get(farm_count_index).updateAveragePersonalIncomeChangeRate();
				farm_count_index++;	
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (Buffer != null) Buffer.close();
			} catch (IOException Exception) {
				Exception.printStackTrace();
			}
		}
	}
	
	/**
	 * Read preferences of each farm for each activity and build preference object
	 * @return matrix of the farm region preferences
	 */
	private FarmDataMatrix getPreferences() {
		String Line;
		ArrayList<String> matrixRow;
		BufferedReader Buffer = null;	
		FarmDataMatrix preferences = new FarmDataMatrix();

		try {
			Buffer = new BufferedReader(new FileReader(ActivityPreferenceFile));
			Line = Buffer.readLine();
			matrixRow = CSVtoArrayList(Line);
			matrixRow.remove(0);
			preferences.setDataElementName(matrixRow);
			
			while ((Line = Buffer.readLine()) != null) {                       // Read row data
				matrixRow = CSVtoArrayList(Line);
				preferences.setFarmMap(matrixRow);
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (Buffer != null) Buffer.close();
			} catch (IOException Exception) {
				Exception.printStackTrace();
			}
		}
		
		return preferences;
	}
	
	/** 
	 * read years of experience file 
	 * @return object corresponding to years performing activity for each farm
	 */
	private FarmDataMatrix getExperience() {
		String Line;
		ArrayList<String> matrixRow;
		BufferedReader Buffer = null;	
		FarmDataMatrix experience = new FarmDataMatrix();

		try {
			Buffer = new BufferedReader(new FileReader(PerformingYearsFile));
			Line = Buffer.readLine();
			matrixRow = CSVtoArrayList(Line);
			matrixRow.remove(0);
			experience.setDataElementName(matrixRow);
			
			while ((Line = Buffer.readLine()) != null) {                       // Read row data
				matrixRow = CSVtoArrayList(Line);
				experience.setFarmMap(matrixRow);
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (Buffer != null) Buffer.close();
			} catch (IOException Exception) {
				Exception.printStackTrace();
			}
		}
		
		return experience;
		
	}
	
	/**
	 * Read a CSV file that specifies each farm and generate a star network for each listed farm
	 * Each farm id/name is set as the root of the star graph, and each associated node has an associated link weight
	 * Each farm will have an individual graph set based on the master list produced in this method
	 * @return List of graphs for each farm
	 */
	private List<Graph<String, DefaultEdge>> getSocialNetworks(){
		List<Graph<String, DefaultEdge>> NetworkList = new ArrayList<Graph<String, DefaultEdge>>();
		
		BufferedReader Buffer = null;	
		String Line;
		ArrayList<String> data;
		ArrayList<String> FarmNames;
		DefaultEdge edge;
		
		try {
			Buffer = new BufferedReader(new FileReader(SocialNetworkFile));
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
					else {
						edge = g.addEdge( data.get(0), FarmNames.get(i) );
						g.setEdgeWeight(edge, Double.parseDouble(data.get(i+1)) );
					}
				}
				NetworkList.add(g);
			}
			Buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return NetworkList;
	}

	/**
	 * Create list of activity type/category by reading the years of experience file
	 * This is used to generate the individual farm activities lists
	 * @return List of activities in the master CSV file
	 */
	public List<Activity> getActivityList() {
		String Line;
		List<Activity> activities = new ArrayList<Activity>();
		ArrayList<String> activityRow;
		BufferedReader Buffer = null;	

		try {
			Buffer = new BufferedReader(new FileReader(PerformingYearsFile));
			Line = Buffer.readLine();									       // first line to be deleted
			activityRow = CSVtoArrayList(Line);
			int ID = 100;
			activityRow.remove(0);
			for (String act: activityRow) {
				ID = ID + 100;
				
				Activity activity = new Activity(ID, act);
				activities.add(activity);
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (Buffer != null) Buffer.close();
			} catch (IOException Exception) {
				Exception.printStackTrace();
			}
		}
		return activities;
	}
	
	/**
	 * This function converts data from CSV file into array structure 
	 * @param CSV String from input CSv file to break into array
	 * @return Result ArrayList of strings 
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
