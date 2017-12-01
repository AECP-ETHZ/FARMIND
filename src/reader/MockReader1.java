package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import agent.farm.DefaultMember;
import agent.farm.Farm;
import product.Crop;
import product.Livestock;
import product.Product;
import socialnetworks.NetworkNode;
import socialnetworks.SocialNetwork;
import socialnetworks.SocialNetworks;

public class MockReader1 implements Reader {

	@Override
	public SocialNetworks getSocalNetwroks() {
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
		
		BufferedReader Buffer = null;		
		try {
			String Line;
			List<Farm> farms = new ArrayList<Farm>();
			ArrayList<String> farmParameters;
			int age;
			int education;
			int memory;
			
			Calendar now = Calendar.getInstance();   // Gets the current date and time
			int currentYear = now.get(Calendar.YEAR); 
			
			Buffer = new BufferedReader(new FileReader("./data/farm_data.csv"));
			
			// Read farm's parameters line by line
			while ((Line = Buffer.readLine()) != null) {
				// How to ignore the first line, which are the titles
				farmParameters = CSVtoArrayList(Line);
				System.out.println("ArrayList data: " + farmParameters);
				// Generate an unique ID for each farm, in the format of "farm001"
				Farm farm001 = new Farm();
				farm001.setFarmId("Farm001");
				DefaultMember farm001Head = new DefaultMember(34,19,5);
				age = currentYear - Integer(farmParameters.get(3));
				education = Integer(farmParameters.get(4));
				memory = Integer(farmParameters.get(5));
				farm001Head.setAge(age);
				farm001Head.setEducation(education);
				farm001Head.setMemory(memory);
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
	}
	
	farms.add(farm001);
	return farms;
	

	private int Integer(String string) {
		// TODO Auto-generated method stub
		return 0;
	}

	// Utility which converts CSV to ArrayList using Split Operation
	public static ArrayList<String> CSVtoArrayList(String CSV) {
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
