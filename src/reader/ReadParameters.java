package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import agent.farm.Person;
import product.Crop;
import product.Crop.CropCategory;
import product.Livestock;
import product.Livestock.LivestockCategory;
import product.Product;
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
		int age = 0;
		int education = 0;
		int memory = 0;
		int entrepreneurship = 0;
		List<Product> preferences = new ArrayList<Product>();
		BufferedReader Buffer = null;	
		int index = 0;
		Product p = null;
		
		try {
			Calendar now = Calendar.getInstance();                             // Gets the current date and time
			int currentYear = now.get(Calendar.YEAR); 
			Buffer = new BufferedReader(new FileReader("./data/farm_data.csv"));
			Line = Buffer.readLine();									       // first line to throw away
			
			while ((Line = Buffer.readLine()) != null) {                       // Read farm's parameters line by line
				farmParameters = CSVtoArrayList(Line);
				System.out.println("ArrayList data: " + farmParameters);
				Farm farm = new Farm();
				
				farm.setFarmId("Farm" + String.format("%03d", index) );		   // index is used to set the actual farm id value
				
				age = currentYear - Integer.parseInt( farmParameters.get(3));
				education = Integer.parseInt( farmParameters.get(4) );
				memory = Integer.parseInt( farmParameters.get(5));
				entrepreneurship = Integer.parseInt( farmParameters.get(6));
				
				int len = farmParameters.size();
				
				for (int i = 7; i < len; i++) {							       // check the element against all possible enum values and add to parameter
					if (farmParameters.get(i) != null) {
						for (LivestockCategory x: LivestockCategory.values() ) {
							String xx = x.toString();
							if (xx.equalsIgnoreCase( farmParameters.get(i))) {
								p = new Livestock(farmParameters.get(i));
								preferences.add(p);
							}
						}
						
						for (CropCategory x: CropCategory.values() ) {
							String xx = x.toString();
							if (xx.equalsIgnoreCase( farmParameters.get(i))) {
								p = new Crop(farmParameters.get(i));
								preferences.add(p);
							}
						}
					}
				}
				
				Person farmHead = new Person(age, education,memory, entrepreneurship, preferences);          // create new farm head and add to farm
				preferences.clear();
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
