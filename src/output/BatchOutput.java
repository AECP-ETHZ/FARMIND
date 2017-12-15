package output;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import product.Product;

public class BatchOutput implements Output {
	private String name;
	private Product p;
	
	public BatchOutput(String farmName, Product p) {
		this.name = farmName;
		this.p = p;
	}

	public void write() {
		PrintWriter writer;
		try {
			writer = new PrintWriter( String.format("./BatchFiles/%s.txt",this.name ));
			writer.println(String.format("Farm Name: %s",this.name));
			writer.println(String.format("Main Product: %s", this.p.getName() ));
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
