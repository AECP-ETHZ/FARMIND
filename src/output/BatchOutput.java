package output;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import decision.DecisionResult;
import product.Product;

public class BatchOutput implements Output {
	private String farmId;
	private List<Product> p;
	
	public BatchOutput(String farmName, List<Product> p) {
		this.farmId = farmName;
		this.p = p;
	}

	public BatchOutput(DecisionResult decision) {
		this.farmId = decision.getFarmId();
		this.p = decision.getProducts();
	}

	/** 
	 * Output text file as a batch file for farmdyn/dairydyn program
	 * Use class fields from constructor to create batch
	 */
	public void write() {
		PrintWriter writer;
		try {
			writer = new PrintWriter( String.format("./BatchFiles/%s.txt",this.farmId ));
			writer.println(String.format("Farm ID: %s",this.farmId));
			writer.println(String.format("Main Product: %s", this.p.get(0).getName() ));
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
