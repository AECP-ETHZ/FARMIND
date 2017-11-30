package consumat;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import agent.farm.DefaultMember;

public class BatchOutput {
	
	public void generateBatch() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("./outFile/output.csv");
		writer.println("The first line");
		writer.println("The second line");
		writer.close();
	}
}
