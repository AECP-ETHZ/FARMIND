package consumat;

import java.io.BufferedReader;
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

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		double max_parameter_length = getParameterCount();
		String origFileName = createFileName();
		String FileName = origFileName + String.format("%d",0);
		long line_counter = 0;
		int file_counter = 1;
		
		System.out.println("Starting Model");
		System.out.print("[");
		
		max_parameter_length = 2;
		for (double parameterSet = 1; parameterSet < max_parameter_length; parameterSet++) {	   // sensitivity testing, loop through all parameters
			ReadParameters reader = new ReadParameters();										   // read all input data files
			List<Farm>     allFarms = reader.getFarms((int)parameterSet);					       // build set of farms with new parameters
			double income = 0;																	   // specific income of farm 
			double probability = 0;																   // probability of income occurring
			List<Double> incomes = new ArrayList<Double>();										   // list of all farm incomes
			initializeRegionIncomeChangePercent(allFarms);										   // only take into account the preset values
			
			if( (parameterSet % Math.round((max_parameter_length/10))) == 0) {					   // output status
				System.out.print("|");
			}
			
			for (int year = 1; year <= 3; year++) {											   // run simulation for a set of years, getting updated income and products	
				int farmIncomeCounter = 0;
				NormalDistribution normal = new NormalDistribution(50000.0, 10000.0);			   // distribution of possible incomes
				
				for (Farm farm : allFarms) {
					if (year == 1) {															   // ignore first year as we already have that initialized with farmdata input file
						income = -1;
						probability = 0.5;
					} else {
						income = incomes.get(farmIncomeCounter)*100;
						probability = normal.cumulativeProbability(income);
					}
					
					farm.updateFarmData(allFarms, income, probability);
					List<String> actionSet = farm.makeDecision(allFarms);             
					DecisionResult decision = new DecisionResult(farm.getPreferences().getProductName(), farm.getFarmName(), actionSet, year, farm.getParameters(), farm.getStrategy(), farm.getIncomeHistory().get(0), farm.getCurrentActivities() );

					line_counter++;
					if (line_counter > 999999) {
						FileName = origFileName + String.format("%d",file_counter);
						file_counter++;
						line_counter = 0;
					} 
					farmIncomeCounter++;
					decision.appendDecisionFile(FileName);
					farm.updateExperiencePlusAge();                              				   // each time period update experience
				}
				List<Object> data = readIncome(allFarms.size());
				incomes = (List<Double>) data.get(0);
				for (Farm farm : allFarms) {
					farm.setCurrentActivites((List<Activity>) data.get(1));
				}

				updateRegionIncomeChangePercent(allFarms,incomes);						   // after time step update the percent change for population
			}
		}
		System.out.println("]");
		System.out.println("Complete");
	}
	
	/**
	 * Automatically generate list of income values for each farm
	 * @param numberOfFarms is number of farms in income list
	 * @return list of incomes and actions
	 */
	private static List<Object> readIncome(int numberOfFarms) {													 
		ReadParameters reader = new ReadParameters();										   // read all input data files
		List<Object> data = reader.readIncomeResults();
		return data;
	}

	/** 
	 * Initialize regional income change for this year based on initial income data
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


