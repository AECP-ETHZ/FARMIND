package output;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class BatchOutput implements Output {
	
	public void write() {
		PrintWriter writer;
		try {
			writer = new PrintWriter("./outFile/output.csv");
			writer.println("The first line");
			writer.println("The second line");
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
