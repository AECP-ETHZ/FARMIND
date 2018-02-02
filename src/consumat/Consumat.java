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
		
		int max_parameter_length = getLineCount();
		String origFileName = createFileName();
		String FileName = "";
		long line_counter = 0;
<<<<<<< HEAD
		int file_counter = 1;
		max_parameter_length = 2;
		
		for (int parameterSet = 1; parameterSet < max_parameter_length; parameterSet++) {		   // sensitivity testing, loop through all parameters
=======

		//max_parameter_length = 2; // WARNING: don't commit resulting file if max is used - file is too large
		for (int parameterSet = 1; parameterSet < max_parameter_length; parameterSet++) {							   // sensitivity testing, loop through all parameters
			
>>>>>>> ed24becf7d3936f5391dde9924d9db5abbdadf64
			ReadParameters reader = new ReadParameters();										   // read all input data files
			List<Farm>     allFarms = reader.getFarms(parameterSet);							   // build set of farms with new parameters
			setNetworkAverageIncome(allFarms);
			double income, probability;															   // income value, and probability of income
			NormalDistribution normal = new NormalDistribution(50000.0, 10000.0);				   // distribution of possible incomes
			
			for (int year = 1; year <= 10; year++) {											   // run simulation for a set of years, getting updated income and products	
				for (Farm farm : allFarms) {
					income = (int)normal.sample();
					probability = normal.cumulativeProbability(income);
					
					if (year == 1) {															   // ignore first year as we already have that initialized with farmdata input file
						income = -1;
						probability = 0.5;
					}
					
					List<List<String>> fullAndMinSetProducts = farm.makeDecision(allFarms, income, probability);             // first list is full set, second list is fake LP product list
					DecisionResult decision = new DecisionResult(farm.getPreferences().getProductName(), farm.getFarmName(), fullAndMinSetProducts.get(0), year, farm.getParameters(), farm.getStrategy(), fullAndMinSetProducts.get(1), income );

					line_counter++;
					if (line_counter > 999999) {
						FileName = origFileName + String.format("%d",file_counter);
						file_counter++;
						line_counter = 0;
					} else {
						FileName = origFileName + String.format("%d",0);
					}
					decision.appendDecisionFile(FileName);
					
					farm.updateExperiencePlusAge();                              				           // each time period update experience
				}
				
				setNetworkAverageIncome(allFarms);				
				System.out.println();
			}
		}
	}
	
	private static void setNetworkAverageIncome(List<Farm> allFarms) {
		double average = 0;
		
		for (Farm farm: allFarms) {
			List<Double> income = new ArrayList<Double>(farm.getIncomeHistory());
			income.remove(0);
			average = average + mean(income);
		}
		average = average/allFarms.size();
		
		for (Farm farm: allFarms) {
			farm.setLastYearNetworkIncomeAverage(average);
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
	
	public static int getLineCount() {
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


