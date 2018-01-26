package consumat;

import java.util.List;

import agent.farm.Farm;
import reader.ReadParameters;

public class Consumat {

	public static void main(String[] args) {
		
		ReadParameters reader = new ReadParameters();
		List<Farm>     farms = reader.getFarms(8);
		double[] income = {10000,30000,10000};
		
		for (int years = 0; years < 3; years++) {
			// get updated products and income from farmydyn
			// farms.updateIncome();
			// farms.updateProducts();

			// simulate all farms for time period t
			for ( int i = 0; i < farms.size(); i++) {
				List<String> p = farms.get(i).getUpdatedActions(farms, income[years]);
				
				String id = farms.get(i).getFarmName();
				System.out.println(id + " " + p.toString());

				farms.get(i).updateExperience();                               // each time period update experience
			}
			System.out.println();

			// run farm dyn with newly generated batch files for all farms
			System.out.println();
		}
		
	}
}
