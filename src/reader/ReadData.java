package reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import activity.Activity;
import agent.Farm;
import agent.Location;
import agent.Person;
import static decision.DecisionResult.strategySets;                            

/** 
 * Read input parameters from configuration text files for each agent, as well as read results data from gams simulations
 * @author kellerke
 *
 */
public class ReadData {

	public static final int NAME = 0;										   
	public static final int COORDINATE1 = 1;
	public static final int COORDINATE2 = 2;
	public static final int AGE = 3;										   // Agent age
	public static final int EDUCATION = 4;									   // Agent education level
	public static final int MEMORY = 5;										   // Agent memory length
	public static final int BETA = 6;										   // experience multiplier
	public static final int BETA_S = 7;									       // social learning multiplier
	public static final int ASPIRATION_COEF = 8;							   // set aspiration level
	public static final int INCOME_TOLERANCE = 9;							   // set income change dissimilarity tolerance
	public static final int ACTIVITY_TOLERANCE = 10;						   // set dissimilarity in activity tolerance
	public static final int LAMBDA = 11;									   // used for satisfaction calculation
	public static final int ALPHA_PLUS = 12;								   // used for satisfaction calculation
	public static final int ALPHA_MINUS = 13;								   // used for satisfaction calculation
	public static final int PHI_PLUS = 14;								       // used for satisfaction calculation
	public static final int PHI_MINUS = 15;  								   // used for satisfaction calculation
	public static final int START_ACTION_INDEX = 16;					       // read in three activities
	public static final int INCOME_INDEX = 19;								   // read in income history

	public String DataFile = "./data/farm_data.csv";					       // allow external function to set data files for testing
	public String PreferenceFile = "./data/products_preference.csv";
	public String YearsFile = "./data/farming_years.csv";
	public String SocialNetworkFile = "./data/social_networks.csv";
	public String ActivityFile = "./data/activities.csv";
	
	/** 
	 * Read the income and activity data from the gams output simulation file after each gams simulation. Use the StrategySet matrix defined in DecisionResult to get correct combinations. 
	 * @see decision.DecisionResult
	 * @return List that contains the income for all farms, and the activity for all farms (two lists)
	 */
	public List<Object> readIncomeResults() {
		List<Double> incomes = new ArrayList<Double>();						   // list of incomes from result file
		List<Activity> strat = new ArrayList<Activity>();					   // list of selected strategies for each agent (one per agent)
		List<Object> ret = new ArrayList<Object>();							   // object to return
		BufferedReader Buffer = null;	 									   // read input file
		String Line;														   // read each line of the file individually
		ArrayList<String> dataArray;										   // separate data line
		List<Activity> activities = getActivityList();						   // generated activity list with ID and name 
		
		File f = new File("Grossmargin_P4,00.csv");							   // actual results file
		while (!f.exists()) {try {
			Thread.sleep(1000);												   // wait until simulation finishes running
		} catch (InterruptedException e) {
			e.printStackTrace();
		}}

		try {
			Buffer = new BufferedReader(new FileReader("Grossmargin_P4,00.csv"));
			System.out.println("read margin values");
			Line = Buffer.readLine();
			while ((Line = Buffer.readLine()) != null) {                       
				dataArray = CSVtoArrayList(Line);						       // Read farm's parameters line by line
				incomes.add( Double.parseDouble(dataArray.get(1)) );
				
				String pre = dataArray.get(2);								   // we need to break the results file which has pre and post strategies
				pre = pre.substring(4);										   // into corresponding strategy values in our system based on the defined strategies
				String post = dataArray.get(3);
				post = post.substring(5);
				
				int[] strategy = {Integer.valueOf(post),Integer.valueOf(pre)};
				int index = 0;
				for(int i = 0; i < strategySets.length; i++) {				   // strategySets were defined in DecisionResult to allow the correct output combinations to be set for gams
					int[] test = {strategySets[i][0],strategySets[i][1]};
					if (Arrays.equals(strategy, test)) {
						index = i;
					}
				}
				
				for(int i = 0; i < activities.size(); i++) {
					if (activities.get(i).getName().equals(String.format("strat%d", index) )) {
						int ID = activities.get(i).getID();
						Activity p = new Activity(ID, String.format("strat%d", index)); 
						strat.add(p);
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}									       

		try {
			Buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ret.add(incomes);
		ret.add(strat);
		return ret;
	}
	
	/**
	 * Each farm in the list contains a social network, the associated people, and preferred activities
	 * The satisfaction and Information Seeking Behavior (ISB) are generated initially
	 * @return List of all farm objects from the input csv file
	 * @param parameterSet indicates which row (or set) of parameters should be used for the configuration
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
		double beta = 0;
		double beta_s = 0;
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
		
		try {
			Calendar now = Calendar.getInstance();                             // Gets the current date and time
			int currentYear = now.get(Calendar.YEAR); 
			Buffer = new BufferedReader(new FileReader(DataFile));
			Line = Buffer.readLine();									       // first line with titles to throw away
			
			while ((Line = Buffer.readLine()) != null) {                       
				farmParameters = CSVtoArrayList(Line);						   // Read farm's parameters line by line
				
				Location location = new Location();							   // create new location for each farm
				List<Activity> currentActivities = new ArrayList<Activity>();  // each farm has list of activities
				List<Double> income = new ArrayList<Double>();				   // each farm has income history records
				double[] coordinates = {0,0};								   // location of farm
				double personalIncomeAverage = 0;					           // personal income average
				
				name = farmParameters.get(NAME);
				coordinates[0] = Double.parseDouble(farmParameters.get(COORDINATE1));
				coordinates[1] = Double.parseDouble(farmParameters.get(COORDINATE2));
				location.setCoordinates(coordinates);
				
				age = currentYear - Integer.parseInt( farmParameters.get(AGE));
				education = Integer.parseInt( farmParameters.get(EDUCATION) );
				memory = Integer.parseInt( farmParameters.get(MEMORY));
				if (memory < 4) {
					memory = 4; 											   // error in calculations if memory is less than 4
				}
				
				beta = Double.parseDouble( farmParameters.get(BETA));
				beta_s = Double.parseDouble( farmParameters.get(BETA_S));
				aspiration_coef = Double.parseDouble( farmParameters.get(ASPIRATION_COEF));
				activity_tolerance = Double.parseDouble( farmParameters.get(ACTIVITY_TOLERANCE));
				income_tolerance = Double.parseDouble( farmParameters.get(INCOME_TOLERANCE));
				
				lambda = Double.parseDouble( farmParameters.get(LAMBDA));
				alpha_plus = Double.parseDouble( farmParameters.get(ALPHA_PLUS));
				alpha_minus = Double.parseDouble( farmParameters.get(ALPHA_MINUS));
				phi_plus = Double.parseDouble( farmParameters.get(PHI_PLUS));
				phi_minus = Double.parseDouble( farmParameters.get(PHI_MINUS));
				
				currentActivities.clear();
				for (int k = START_ACTION_INDEX; k < farmParameters.size(); k++) {
					for(int i = 0; i<activities.size(); i++) {
						if (activities.get(i).getName().equals(farmParameters.get(k) )) {
							int ID = activities.get(i).getID();
							Activity p = new Activity(ID, farmParameters.get(k)); 
							currentActivities.add(p);
						}
					}
				}
				
				for (int i = INCOME_INDEX; i < (INCOME_INDEX + memory); i++) {
					income.add( Double.parseDouble( farmParameters.get(i) ) );
				}
				
				List<Double> avgIncome = new ArrayList<Double>(income);
				avgIncome.remove(0);                                           // remove income of first time period
				personalIncomeAverage = mean(avgIncome);

				Person farmHead = new Person(age, education, memory);        
				Farm farm = new Farm(name, location, network.get(farm_count_index), 
						income, personalIncomeAverage, experience, preference, activities, 
						activity_tolerance, income_tolerance, currentActivities, farmHead, 
						beta, beta_s, aspiration_coef, lambda, alpha_plus, alpha_minus, phi_plus, phi_minus);
				
				farm.setLearningRate();
				
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
		return farms;
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
			Buffer = new BufferedReader(new FileReader(PreferenceFile));
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
			Buffer = new BufferedReader(new FileReader(YearsFile));
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
	 * Read a csv file that specifies each farm and generate a star network for each listed farm
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
	 * Create list of activity type/category from master CSV list
	 * This is used to generate the individual farm product lists
	 * @return List of activities in the master CSV file
	 */
	private List<Activity> getActivityList() {
		String Line;
		List<Activity> activities = new ArrayList<Activity>();
		ArrayList<String> activityRow;
		BufferedReader Buffer = null;	

		try {
			Buffer = new BufferedReader(new FileReader(ActivityFile));
			Line = Buffer.readLine();									       // first line to throw away
			
			while ((Line = Buffer.readLine()) != null) {                       // Read activity data
				activityRow = CSVtoArrayList(Line);
				
				int ID = Integer.parseInt(activityRow.get(0));
				String name = activityRow.get(1);
				
				Activity activity = new Activity(ID, name);
				
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
	 * Input a readline from a csv using a split operation 
	 * @param CSV String from input csv file to break into array
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
	
	/** 
	 * Return mean value of provided list 
	 * @param list of values to calculate mean with
	 * @return mean
	 */
	private double mean(List<Double> list) {
		double mean = 0;												       // mean value to return
		
		for (int i = 0; i<list.size(); i++) {
			mean = mean + list.get(i);
		}
		
		return mean / list.size();
	}

}
