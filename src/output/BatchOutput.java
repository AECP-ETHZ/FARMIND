package output;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import decision.DecisionResult;
import product.Product;

public class BatchOutput implements Output {
	private String farmId;
	private Product p;
	
	public BatchOutput(String farmName, Product p) {
		this.farmId = farmName;
		this.p = p;
	}

	public BatchOutput(DecisionResult decision) {
		this.farmId = decision.getFarmId();
		this.p = decision.getProducts().get(0);
	}

	public void write() {
		PrintWriter writer;
		try {
			writer = new PrintWriter( String.format("./BatchFiles/%s.txt",this.farmId ));
			writer.println(String.format("Farm ID: %s",this.farmId));
			writer.println(String.format("Main Product: %s", this.p.getName() ));
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
