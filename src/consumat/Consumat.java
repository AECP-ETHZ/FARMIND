package consumat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import decision.DecisionResult;
import reader.ReadParameters;

import org.apache.commons.math3.distribution.NormalDistribution;

import activity.Activity;
import agent.Farm;

/** 
 * Full ABM simulation runs inside of main by creating farm objects and making decisions for each farm.
 * @author kellerke
 *
 */
public class Consumat {
	static long line_counter = 0;
	static int file_counter = 1;
	static String origFileName = createFileName();										           // file name for logging
	static String FileName = origFileName + String.format("%d",0);								   // given enough lines in the log file, we need to start a new file
		
	public static void main(String[] args) {
		double max_parameter_length = getParameterCount();
		System.out.println("Starting Model");
		
		//max_parameter_length = 2;
		for (double parameterSet = 1; parameterSet < max_parameter_length; parameterSet++) {	   // sensitivity testing, loop through all parameters
			ReadParameters reader = new ReadParameters();										   // read all input data files
			List<Farm>     allFarms = reader.getFarms((int)parameterSet);					       // build set of farms with new parameters
			List<Double> simulatedIncomeForFarms = new ArrayList<Double>();						   // list of all farm incomes
			
			initializeRegionIncomeChangePercent(allFarms);										   // only take into account the preset values
			
			for (int year = 1; year <= 4; year++) {											       // run simulation for a set of years, getting updated income and products	
				System.out.println(String.format("year %d", year));				
				
				CreateGamsFile(allFarms, year, simulatedIncomeForFarms);
				RunGams(allFarms);
				simulatedIncomeForFarms = readGamsResults(allFarms);
				updateRegionIncomeChangePercent(allFarms,simulatedIncomeForFarms);				   // after time step update the percent change for population
			}
		}

		System.out.println("Complete");
	}
	
	/**
	 * Using the list of all farms, update the incomes for the farms, and then make the farm decision and resulting strategy choice for each farm. 
	 * Each farm then appends this choice to the simulation control file
	 * @param allFarms is the list of all farms in the model
	 * @param year indicates which time period
	 * @param simulatedIncomeForFarms is the income from the previous time period simulation
	 */
	private static void CreateGamsFile(List<Farm> allFarms, int year, List<Double> simulatedIncomeForFarms) {
		double income = 0;																	       // specific income of farm 
		double probability = 0;																       // probability of income occurring
		NormalDistribution normal = new NormalDistribution(50000.0, 10000.0);			           // distribution of possible incomes
		int farmIndex = 0;																		   // index of specific farm in list

		File f = new File("p_allowedStratPrePost.csv");										       // delete last time period's simulation file
		if (f.exists()) {f.delete();}
		
		for (Farm farm : allFarms) {
			if (year == 1) {															           // ignore first year as we already have that initialized with input file
				income = -1;
				probability = 0.5;
			} else {
				income = simulatedIncomeForFarms.get(farmIndex)*100;
				probability = normal.cumulativeProbability(income);
			}
			
			farm.updateFarmData(allFarms, income, probability);
			List<String> possibleActivitySet = farm.makeDecision(allFarms);      
			
			System.out.print(farm.getFarmName() + " current activity: ");
			for (Activity act: farm.getCurrentActivities() ) System.out.print(act.getName() + " ");
			System.out.print("\n");
			
			System.out.print(farm.getFarmName() + " possible activity: ");
			for (String act: possibleActivitySet) System.out.print(act + " ");
			System.out.print("\n");
			
			DecisionResult decision = new DecisionResult(farm.getPreferences().getDataElementName(), farm.getFarmName(), year, farm.getParameters(), farm.getStrategy(), farm.getIncomeHistory().get(0), farm.getCurrentActivities(), possibleActivitySet);

			line_counter++;
			if (line_counter > 999999) {
				FileName = origFileName + String.format("%d",file_counter);
				file_counter++;
				line_counter = 0;
			} 
			decision.appendDecisionFile(FileName);
			decision.appendGamsFile();													   // create a file 'p_allowedStrat' which contains the gams options for each farm
			farm.updateExperiencePlusAge();                              				   // each time period update experience
			farmIndex++;
		}

		System.out.println("Created Gams simulation file");
	}
	
	/** 
	 * Runs gams simulation based on input file created while looping through all farms.
	 * @param allFarms list of all farms in system
	 */
	private static void RunGams(List<Farm> allFarms) {
		Runtime runtime = Runtime.getRuntime();								   // java runtime to run commands
		
		File f = new File("Grossmargin_P4,00.csv");							   // delete previous results file before new run
		f.delete();
		
		try {
			String name = System.getProperty("os.name").toLowerCase();;
			if (name.startsWith("win") ){
				runtime.exec("cmd /C" + "run_gams.bat");						   // actually run command
			}
			if (name.startsWith("mac")) {
				runtime.exec("/bin/bash -c ./run_gams_mac.command");				   // actually run command
			}
			System.out.println("Starting gams model");
			System.out.println("Waiting for gams results to be generated");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Read gams file and set the income and activities based on that file
	 * @param allFarms list of all farms in system
	 * @return incomes is a list of all incomes for each farm
	 */
	@SuppressWarnings("unchecked")
	private static List<Double> readGamsResults(List<Farm> allFarms) {
		List<Double> incomes = new ArrayList<Double>();						   // list of all farm incomes
		List<Activity> activity = new ArrayList<Activity>();	   	 	       // list of all farm activities selected by LP model
		NormalDistribution normal = new NormalDistribution(0, 0.2);			   // distribution of variance to income for all farms

		double incomeVariance = normal.sample();							   // vary reported income by sampled amount
		
		List<Object> data = readIncome();            					       // read all results data
		incomes = (List<Double>) data.get(0);
		activity = (List<Activity>) data.get(1);
		
		for (int i = 0; i<incomes.size(); i++) {
			double newIncome = incomes.get(i) + incomes.get(i)*incomeVariance; // vary by equal amount for all regions
			incomes.set(i, newIncome);
		}
		
		for (int i = 0; i < allFarms.size(); i++) {
			List<Activity> act = new ArrayList<Activity>();
			act.add(activity.get(i));
			allFarms.get(i).setCurrentActivites(act);
		}
		return incomes;
	}
	
	/**
	 * Automatically generate list of income values for each farm
	 * @return list of incomes and actions
	 */
	private static List<Object> readIncome() {													 
		ReadParameters reader = new ReadParameters();										   // read all input data files
		List<Object> data = reader.readIncomeResults();									
		return data;
	}

	/** 
	 * Initialize regional income change for this year based on initial income data.
	 * @param allFarms is list of all farms in region
	 */
	private static void initializeRegionIncomeChangePercent(List<Farm> allFarms) {
		double historicalRegionAverage = 0;
		List<Double> initIncome = new ArrayList<Double>();
		double thisYearAverage = 0;
		double percentChange;
		
		for (Farm farm: allFarms) {
			List<Double> income = new ArrayList<Double>(farm.getIncomeHistory());
			initIncome.add(income.get(0));
			income.remove(0);
			historicalRegionAverage = historicalRegionAverage + mean(income);
		}
		historicalRegionAverage = historicalRegionAverage/allFarms.size();
		thisYearAverage = mean(initIncome);
		
		percentChange = (thisYearAverage - historicalRegionAverage) / historicalRegionAverage;
		
		for (Farm farm: allFarms) {
			farm.setRegionIncomeChangePercent(percentChange);
		}
	}
	
	/** 
	 * Calculate regional income change for this year based on historical income data
	 * @param allFarms is list of all farms in region
	 * @param thisYearIncome is list of income values for each farm
	 */
	private static void updateRegionIncomeChangePercent(List<Farm> allFarms, List<Double> thisYearIncome) {
		double historicalRegionAverage = 0;
		double thisYearAverage = mean(thisYearIncome);
		double percentChange;
		
		for (Farm farm: allFarms) {
			List<Double> income = new ArrayList<Double>(farm.getIncomeHistory());
			income.remove(0);
			historicalRegionAverage = historicalRegionAverage + mean(income);
		}
		historicalRegionAverage = historicalRegionAverage/allFarms.size();
		
		percentChange = (thisYearAverage - historicalRegionAverage) / historicalRegionAverage;
		
		for (Farm farm: allFarms) {
			farm.setRegionIncomeChangePercent(percentChange);
		}
	}
	
	/** 
	 * Return mean value of provided list 
	 * @param list of values to calculate mean with
	 * @return mean
	 */
	private static double mean(List<Double> list) {
		double mean = 0;												       // mean value to return
		
		for (int i = 0; i<list.size(); i++) {
			mean = mean + list.get(i);
		}
		
		return mean / list.size();
	}

	/** 
	 * Create generic file name so version number can be appended to end
	 * @return fileName
	 */
	public static String createFileName() {
		Calendar now = Calendar.getInstance();                             // Gets the current date and time
		int day = now.get(Calendar.DAY_OF_MONTH); 
		int month = now.get(Calendar.MONTH) + 1;
		int year_file = now.get(Calendar.YEAR);
		int hour = now.get(Calendar.HOUR);
		int minute = now.get(Calendar.MINUTE);	
		String fileName = String.format("Results-%d-%d-%d-%d-%d_v", day, month, year_file,hour, minute);
		
		return fileName;
	}
	
	/** 
	 * Get number of parameter options in input parameter file
	 * @return number of possible parameter options
	 */
	public static int getParameterCount() {
		BufferedReader Buffer = null;	
		int count = 0;
		
		try {
			Buffer = new BufferedReader(new FileReader("./data/parameters.csv"));
			while(Buffer.readLine() != null) {
				count++;
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
		return count;
	}
}


