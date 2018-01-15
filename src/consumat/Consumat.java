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
		List<Crop>        crops = reader.getCropList();
		List<Livestock>   livestock = reader.getLivestockList();
		List<Farm>        farms = reader.getFarms();
		
		TransactionCalculator cal = new TransactionCalculator(farms.get(0), crops, livestock, farms);
		
		double cost = 0; 
		
		System.out.println(cost);
		
		for ( int i = 0; i < farms.size(); i++) {

			List<Product> p = farms.get(i).getAction(farms);
			String id = farms.get(i).getFarmId();

			DecisionResult decision = new DecisionResult(id, p);
			
			BatchOutput batch = new BatchOutput(decision);
			batch.write();
		}
	}
}
