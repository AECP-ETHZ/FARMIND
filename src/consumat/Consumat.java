package consumat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import agent.farm.Farm;
import decision.DecisionResult;
import reader.ReadParameters;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Consumat {

	public static void main(String[] args) {
		
		int max_parameter_length = getParameterCount();
		String origFileName = createFileName();
		String FileName = origFileName + String.format("%d",0);
		long line_counter = 0;
		int file_counter = 1;
		
		max_parameter_length = 10;
		for (int parameterSet = 1; parameterSet < max_parameter_length; parameterSet++) {		   // sensitivity testing, loop through all parameters
			ReadParameters reader = new ReadParameters();										   // read all input data files
			List<Farm>     allFarms = reader.getFarms(parameterSet);							   // build set of farms with new parameters
			double income, probability;
			initializeRegionIncomeChangePercent(allFarms);										   // only take into account the preset values

			for (int year = 1; year <= 10; year++) {											   // run simulation for a set of years, getting updated income and products	
				List<List<Double>> incomes = new ArrayList<List<Double>>();
				incomes = generateIncomes(allFarms.size());
				int farmIncomeCounter = 0;
				
				for (Farm farm : allFarms) {
					if (year == 1) {															   // ignore first year as we already have that initialized with farmdata input file
						income = -1;
						probability = 0.5;
					} else {
						income = incomes.get(0).get(farmIncomeCounter);
						probability = incomes.get(1).get(farmIncomeCounter++);
					}
					
					farm.updateFarmData(allFarms, income, probability);
					List<List<String>> fullAndMinSetProducts = farm.makeDecision(allFarms);             // first list is full set, second list is fake LP product list
					DecisionResult decision = new DecisionResult(farm.getPreferences().getProductName(), farm.getFarmName(), fullAndMinSetProducts.get(0), year, farm.getParameters(), farm.getStrategy(), fullAndMinSetProducts.get(1), farm.getIncomeHistory().get(0) );

					line_counter++;
					if (line_counter > 999999) {
						FileName = origFileName + String.format("%d",file_counter);
						file_counter++;
						line_counter = 0;
					} 
					decision.appendDecisionFile(FileName);
					farm.updateExperiencePlusAge();                              				   // each time period update experience
				}
				
				updateRegionIncomeChangePercent(allFarms,incomes.get(0));						   // after time step update the percent change for population
			}
		}
	}
	
	/**
	 * Automatically generate list of income values for each farm
	 * @param year is number of farms in income list
	 * @return
	 */
	private static List<List<Double>> generateIncomes(int year) {													 
		NormalDistribution normal = new NormalDistribution(50000.0, 10000.0);				       // distribution of possible incomes
		List<List<Double>> ret = new ArrayList<List<Double>>();
		List<Double> year_income = new ArrayList<Double>();
		List<Double> income_prob = new ArrayList<Double>();
		
		while(year > 0) {
			double inc = (int)normal.sample();
			year_income.add(inc);
			income_prob.add( normal.cumulativeProbability(inc) );
			year--;
		}
		ret.add(year_income);
		ret.add(income_prob);
		return ret;
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


