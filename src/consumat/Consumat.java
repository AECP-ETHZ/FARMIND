package consumat;

import java.util.List;

import agent.farm.Farm;
import output.BatchOutput;
import reader.ReadParameters;

public class Consumat {

	public static void main(String[] args) {
		
		double avg;
		
		// 1 input parameters
		ReadParameters reader = new ReadParameters();
		
		// 2 create agents
		List<Farm> farms = reader.getFarms();
		
		for ( int i = 0; i < farms.size(); i++) {
			avg = farms.get(i).getSocialTies();
	        System.out.println(String.format("farm network weight is: %f", avg) );
			System.out.println(String.format("Action: %s", farms.get(i).getAction() ));	
			System.out.println(String.format( farms.get(i).getNetwork().toString() ) );
			
			System.out.println();
			
			// 4 output batch file
			BatchOutput batch = new BatchOutput();
			batch.write();
		}

	}
}
