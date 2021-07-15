package mathematical_programming;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList; 
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import activity.Activity;
import agent.Farm;
import reader.ReadData;

/** 
 * Toy model to demonstrate the interface required between the farmind model and the external optimzation program. 
 * In this case there is only a fake model that reads in the preset results file. And the inputsforMP function is blank as there is no setup. 
 * 
 * @author kellerke
 *
 */

public class Toy implements MP_Interface{
	File file; 																   // file object to read/write 							
	List<Object> year_price = new ArrayList<Object>();						   // list of yearly prices for model
	List<Double> yearlyPrices = new ArrayList<Double>();					   // list of modeling prices per year
	List<String> listOfYears = new ArrayList<String>();						   // list of modeling price/years
	String strategyFile;													   // strategy file
	String resultsFile;														   // results file
	String gamsModelFile;													   // gams model file, used for editing actual gams script
	String yearlyPriceFile; 												   // price file for reading yearly prices 
	private static final Logger LOGGER = Logger.getLogger("FARMIND_LOGGING");
	
	public Toy(Properties cmd, int simYear, int memoryLengthAverage) {
		this.resultsFile = String.format("%s\\toy_results.csv",cmd.getProperty("project_folder"));
	}
	
	@Override
	public void inputsforMP(Farm farm, List<String> possibleActivity) {
		LOGGER.info("Create input files required for the model. ");
		// Create the input files required for the model. This would be all the configuration files for the gams code. 
		// This needst to be individually set based on the possible activities for each farm agent
	}

	@Override
	public void runModel(Properties cmd, int nFarms, int year, boolean pricingAverage, int memoryLengthAverage) {
		Runtime runtime = Runtime.getRuntime();						           // java runtime to run commands
		
		LOGGER.info("Waiting for output generated by MP model");
		
		// 1. Create a batch file to start the model:  
		// 2. Run batch file :: "cmd /C" + "run_gams.bat"
		
		LOGGER.info("Creating run_gams.bat file for debug");
		File f = new File("run_gams.bat");
		f.delete();
		FileWriter fw;
		
		// This command just copies the fake results to simulate a model run
		try {
			fw = new FileWriter(f,true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter writer = new PrintWriter(bw);
			writer.println( String.format("copy \".\\%s\\toy_results.csv\" .\\%s\\", 
					cmd.getProperty("data_folder"), cmd.getProperty("project_folder")) );
			LOGGER.fine("copying fake results file");
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			runtime.exec("cmd /C" + "run_gams.bat");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}					   // actually run command
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Double> readMPIncomes(Properties cmd, List<Farm> allFarms) throws FileNotFoundException, IOException {
		List<Double> incomes = new ArrayList<Double>();						   // list of all farm incomes   
		
		List<Object> data = readMPOutputFiles(cmd, allFarms);			       // read data file generated by MP
		incomes = (List<Double>) data.get(0);

		return incomes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ArrayList<Activity>> readMPActivities(Properties cmd, List<Farm> allFarms) throws FileNotFoundException, IOException {
		List<ArrayList<Activity>> activities = new ArrayList<ArrayList<Activity>>();	   	 	   // list of all farm activities selected by MP model
		
		List<Object> data = readMPOutputFiles(cmd, allFarms);			                           // read data file generated by MP
		activities = (List<ArrayList<Activity>>) data.get(1);
		
		if (activities.size() != allFarms.size()) {
			LOGGER.severe("Exiting FARMIND. Gams results do not match expected number of farms.");
			System.exit(0);
		} 
	
		return activities;
	}

	@Override
	public ArrayList<Activity> getExitActivity() {
		// if the agent decides to opt-out we use this to return the correct opt-out activity
		ArrayList<Activity> activities = new ArrayList<Activity>();	   	 	       // list of all farm activities selected by MP model
		Activity exit = new Activity(0,"exit");
		activities.add(exit);
	
		return activities;
	}
	
	/** 
	 * Read the MP output files to get income and activities
	 * @param allFarms: list of all farms in system
	 * @param cmd :: properties object
	 * @return return :: incomes and activities produced by the MP model
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public List<Object> readMPOutputFiles(Properties cmd, List<Farm> allFarms) throws FileNotFoundException, IOException {
		List<Double> incomesFromMP = new ArrayList<Double>();				       // list of all agents' incomes produced by the MP
		List<List<Activity>> activitiesFromMP = new ArrayList<List<Activity>>();   // list of all agents' final activities selected by the MP
		List<Object> incomes_activitiesOutput = new ArrayList<Object>();		   // combination list of incomes and activities to return
		BufferedReader Buffer = null;	 									       // read input file
		String Line;														       // read each line of the file individually
		ArrayList<String> dataArray;										       // separate data line
		ReadData reader = new ReadData(cmd);
		
		List<Activity> allPossibleActivities = reader.getActivityList();		   // generated activity list with ID and name 
		
		File f = new File(this.resultsFile);					   // actual results file
		while (!f.exists()) {try {
			Thread.sleep(1000);												       // wait until the MP finishes running
		} catch (InterruptedException e) {
			e.printStackTrace();
		}}

		try {
			Buffer = new BufferedReader(new FileReader(this.resultsFile));
			Line = Buffer.readLine();
			while ((Line = Buffer.readLine()) != null) {        
				dataArray = CSVtoArrayList(Line);						          // Read farm's parameters line by line
				incomesFromMP.add( Double.parseDouble(dataArray.get(1)) );
				String name = dataArray.get(2);
				int ID = 0;
				
				for(int i = 0; i < allPossibleActivities.size(); i++) {
					if (name.equals(allPossibleActivities.get(i).getName())) {
						ID = i*100+200;
					}
				}
				
				Activity p = new Activity(ID, name); 
				List<Activity> farmActivityList = new ArrayList<Activity>();
				farmActivityList.add(p);
				
				activitiesFromMP.add(farmActivityList);
			}
			
			} catch (IOException e) {
				e.printStackTrace();
			}									       
	
			try {
				Buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		

		incomes_activitiesOutput.add(incomesFromMP);
		incomes_activitiesOutput.add(activitiesFromMP);
		
		return incomes_activitiesOutput;
	}
	
	/**
	 * This function converts data from CSV file into array structure 
	 * @param CSV :: String from input CSV file to break into array
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
