package consumat;

import java.util.List;

import agent.farm.Farm;
import decision.DecisionResult;
import output.BatchOutput;
import product.Crop;
import product.Livestock;
import product.Product;
import reader.FarmProductMatrix;
import reader.ReadParameters;

public class Consumat {

	public static void main(String[] args) {
		
		ReadParameters reader = new ReadParameters();
		
		FarmProductMatrix matrix = reader.getPreferences();
		
		FarmProductMatrix experience = reader.getExperience();
		
		int x = experience.farmProductValue("Farm19", "Chickens");
		
		System.out.println(x);
		
		List<Crop> crops = reader.getCropList();
		List<Livestock> livestock = reader.getLivestockList();
		
		int cost = matrix.getTechDistance("Eggplants", "Tomatoes", crops, livestock);
		
		System.out.println(cost);

		List<Farm> farms = reader.getFarms();
		
		for ( int i = 0; i < farms.size(); i++) {

			List<Product> p = farms.get(i).getAction(farms);
			String id = farms.get(i).getFarmId();

			DecisionResult decision = new DecisionResult(id, p);
			
			BatchOutput batch = new BatchOutput(decision);
			batch.write();
		}
	}
}
