package consumat;

import java.util.List;
import java.util.Random;

import agent.farm.Farm;
import decision.DecisionResult;
import reader.ReadParameters;

public class Consumat {

	public static void main(String[] args) {

		for (int parameterSet = 1; parameterSet < 20; parameterSet++) {															   // sensitivity testing, loop through all parameters

			ReadParameters reader = new ReadParameters();
			List<Farm>     farms = reader.getFarms(parameterSet);											   // build set of farms with new parameters
			Random rand = new Random();
			
			for (int years = 0; years < 3; years++) {											   // run simulation for a set of years, getting updated income and products
				
				for ( int i = 0; i < farms.size(); i++) {                                   	   // simulate all farms for time period t
					double income = rand.nextGaussian()*10+50;
					income = Math.round(income*1000);
					
					List<List<String>> fullAndMinSetProducts = farms.get(i).getUpdatedActions(farms, income);             // first list is full, second list is fake LP product list
					
					String id = farms.get(i).getFarmName();
					//System.out.println(id + " " + fullAndMinSetProducts.toString());
					
					DecisionResult decision = new DecisionResult(id, fullAndMinSetProducts.get(0), years, farms.get(i).getParameters(), farms.get(i).getStrategy(), fullAndMinSetProducts.get(1), income );
					decision.appendDecisionFile();
						
					farms.get(i).updateExperience();                              				   // each time period update experience
				}
				System.out.println();
			}
		}
	}
}
