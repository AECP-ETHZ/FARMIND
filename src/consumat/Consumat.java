package consumat;

import java.util.List;

import agent.farm.Farm;
import reader.ReadParameters;

public class Consumat {

	public static void main(String[] args) {
		
		ReadParameters reader = new ReadParameters();
		List<Farm>     farms = reader.getFarms();
		
		for (int years = 0; years < 1; years++) {
			// get updated products and income from farmydyn
			// farms.updateIncome();
			// farms.updateProducts();
		
			// simulate all farms for time period t
			for ( int i = 0; i < farms.size(); i++) {
				List<String> p = farms.get(i).getAction(farms, 40000.00);
				
				String id = farms.get(i).getFarmName();
				System.out.println(id + " " + p.toString());
				System.out.println();
				farms.get(i).updateExperience();                               // each time period update experience
			}
			
			// run farm dyn with newly generated batch files for all farms
			System.out.println();
		}
		
	}
}
