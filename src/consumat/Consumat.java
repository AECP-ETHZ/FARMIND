package consumat;

import java.util.List;

import agent.farm.Farm;
import reader.ReadParameters;

public class Consumat {

	public static void main(String[] args) {
		
		for (int k = 1; k < 2; k++) {															   // sensitivity testing, loop through all parameters
			ReadParameters reader = new ReadParameters();
			List<Farm>     farms = reader.getFarms(k);											   // build set of farms with new parameters
			
			for (int years = 0; years < 3; years++) {											   // run simulation for a set of years, getting updated income and products
				// get updated products and income from farmydyn
				// farms.updateIncome();
				// farms.updateProducts();
				double[] income = {10000,30000,10000};
	
				for ( int i = 0; i < farms.size(); i++) {                                   	   // simulate all farms for time period t
					List<String> p = farms.get(i).getUpdatedActions(farms, income[years]);
					
					String id = farms.get(i).getFarmName();
					System.out.println(id + " " + p.toString());
					// run linear programming model for farm i at this year
	
					farms.get(i).updateExperience();                              				   // each time period update experience
				}
				System.out.println();
			}
		}
	}
}
