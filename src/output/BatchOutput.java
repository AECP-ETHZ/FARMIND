package output;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import decision.DecisionResult;

public class BatchOutput implements Output {
	private String farmId;
	
	public BatchOutput(String farmName) {
		this.farmId = farmName;
	}

	public BatchOutput(DecisionResult decision) {
		this.farmId = decision.getFarmId();
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
			//writer.println(String.format("Main Product: %s", this.p ));
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
