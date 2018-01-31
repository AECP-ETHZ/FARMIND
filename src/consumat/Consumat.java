package consumat;

import java.util.List;

import agent.farm.Farm;
import decision.DecisionResult;
import reader.ReadParameters;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Consumat {

	public static void main(String[] args) {

		for (int parameterSet = 1; parameterSet < 2; parameterSet++) {							   // sensitivity testing, loop through all parameters

			ReadParameters reader = new ReadParameters();										   // read all input data files
			List<Farm>     allFarms = reader.getFarms(parameterSet);							   // build set of farms with new parameters
			double income, probability;															   // income value, and probability of income
			NormalDistribution normal = new NormalDistribution(50000.0, 10000.0);				   // distribution of possible incomes
			
			for (int year = 0; year < 3; year++) {											       // run simulation for a set of years, getting updated income and products	
				for (Farm farm : allFarms) {
					income = (int)normal.sample();
					probability = normal.cumulativeProbability(income);
					
					List<List<String>> fullAndMinSetProducts = farm.makeDecision(allFarms, income, probability);             // first list is full set, second list is fake LP product list
					DecisionResult decision = new DecisionResult(farm.getFarmName(), fullAndMinSetProducts.get(0), year, farm.getParameters(), farm.getStrategy(), fullAndMinSetProducts.get(1), income );
					decision.appendDecisionFile();
					
					farm.updateExperience();                              				           // each time period update experience
				}
				
				System.out.println();
			}
		}
	}
}
