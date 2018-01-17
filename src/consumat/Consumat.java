package consumat;

import java.util.List;

import agent.farm.Farm;
import calculator.TransactionCalculator;
import decision.DecisionResult;
import output.BatchOutput;
import product.Crop;
import product.Livestock;
import product.Product;
import reader.ReadParameters;

public class Consumat {

	public static void main(String[] args) {
		
		ReadParameters reader = new ReadParameters();
		List<Farm>     farms = reader.getFarms();
		
		for ( int i = 0; i < farms.size(); i++) {

			List<Product> p = farms.get(i).getAction(farms, 40000.00);
			
			farms.get(i).updateExperience();
			
			String id = farms.get(i).getFarmName();

			DecisionResult decision = new DecisionResult(id, p);
			
			BatchOutput batch = new BatchOutput(decision);
			batch.write();
		}
	}
}
