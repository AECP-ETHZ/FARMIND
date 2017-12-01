package consumat;

import java.io.FileNotFoundException;
import java.util.List;

import agent.farm.Farm;
import reader.ReadParameters;

public class Consumat {

	public static void main(String[] args) {

		// 1 input parameters
		ReadParameters reader = new ReadParameters();
		
		// 2 create agents
		List<Farm> farms = reader.getFarms();

		// 3 decision making
		System.out.println(String.format("Action: %s", farms.get(0).getAction() ));	
		
		// 4 output batch file
		BatchOutput batch = new BatchOutput();
		try {
			batch.generateBatch();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
