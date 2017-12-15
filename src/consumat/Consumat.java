package consumat;

import java.util.List;

import agent.farm.Farm;
import decision.DecisionResult;
import output.BatchOutput;
import product.Product;
import reader.ReadParameters;

public class Consumat {

	public static void main(String[] args) {
		
		ReadParameters reader = new ReadParameters();
		
		//List<Crop> crops = reader.getCropList();
		//List<Livestock> livestock = reader.getLivestockList();
		
		List<Farm> farms = reader.getFarms();
		
		for ( int i = 0; i < farms.size(); i++) {
			
			List<Product> p = farms.get(i).getAction();
			String id = farms.get(i).getFarmId();

			DecisionResult decision = new DecisionResult(id, p);
			
			BatchOutput batch = new BatchOutput(decision);
			batch.write();
		}
	}
}
