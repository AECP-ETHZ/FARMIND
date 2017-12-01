package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import agent.farm.Person;
import agent.farm.Farm;
import socialnetworks.NetworkNode;
import socialnetworks.SocialNetwork;
import socialnetworks.SocialNetworks;

public class ReadParameters implements Reader {

	@Override
	public SocialNetworks getSocialNetworks() {
		SocialNetworks socialNetworks = new SocialNetworks();
		// read social networks from input file
		SocialNetwork socialNetwork = new SocialNetwork("Farm001");
		socialNetwork.addNetworkNode(new NetworkNode("Farm002", 0.3));
		socialNetwork.addNetworkNode(new NetworkNode("Farm003", 0.2));
		socialNetwork.addNetworkNode(new NetworkNode("Farm003", 0.5));
		socialNetworks.addSocialNetwork(socialNetwork);
		return socialNetworks;
	}

	@Override
	public List<Farm> getFarms() {
		String Line;
		List<Farm> farms = new ArrayList<Farm>();
		ArrayList<String> farmParameters;
		int age;
		int education;
		int memory;
		int index = 0;
		BufferedReader Buffer = null;		
		
		try {
			Calendar now = Calendar.getInstance();                             // Gets the current date and time
			int currentYear = now.get(Calendar.YEAR); 
			Buffer = new BufferedReader(new FileReader("./data/farm_data.csv"));
			Line = Buffer.readLine();									       // first line to throw away
			
			while ((Line = Buffer.readLine()) != null) {                       // Read farm's parameters line by line
				farmParameters = CSVtoArrayList(Line);
				System.out.println("ArrayList data: " + farmParameters);
				Farm farm = new Farm();
				
				farm.setFarmId("Farm" + String.format("%03d", index) );
				
				age = currentYear - Integer.parseInt( farmParameters.get(3));
				education = Integer.parseInt( farmParameters.get(4) );
				memory = Integer.parseInt( farmParameters.get(5));
				
				Person farmHead = new Person(age, education,memory);
				farm.setHead(farmHead);
				
				farms.add(farm);
				index++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (Buffer != null) Buffer.close();
			} catch (IOException Exception) {
				Exception.printStackTrace();
			}
		}
		return farms;
	}

	public static ArrayList<String> CSVtoArrayList(String CSV) {		       // Utility which converts CSV to ArrayList using Split Operation
		ArrayList<String> Result = new ArrayList<String>();
		
		if (CSV != null) {
			String[] splitData = CSV.split("\\s*,\\s*");
			for (int i = 0; i < splitData.length; i++) {
				if (!(splitData[i] == null) || !(splitData[i].length() == 0)) {
					Result.add(splitData[i].trim());
				}
			}
		}
		
		return Result;
	}

}
