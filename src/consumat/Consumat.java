package consumat;

import java.util.List;

import agent.farm.Farm;
import output.BatchOutput;
import product.Crop;
import product.Livestock;
import product.Product;
import reader.ReadParameters;

public class Consumat {

	public static void main(String[] args) {
		
		// 1 input parameters
		ReadParameters reader = new ReadParameters();
		
		List<Crop> crops = reader.getCropList();
		List<Livestock> livestock = reader.getLivestockList();
		
		// 2 create agents
		List<Farm> farms = reader.getFarms();
		
		for ( int i = 0; i < farms.size(); i++) {
			
			
			// update farms with dairydyn simulation results
			
			// rerun simulation
			
			Product p = farms.get(i).getAction().get(0);
			
			System.out.println(String.format("Action: %s", p.getName()  ));	

			System.out.println();
			
			// 4 output batch file
			BatchOutput batch = new BatchOutput();
			batch.write();
			
			// run terminal command (dairydyn)
		}

	}
}
