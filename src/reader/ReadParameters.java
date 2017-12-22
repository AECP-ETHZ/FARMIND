package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import agent.farm.Person;
import product.Crop;
import product.Livestock;
import product.Product;
import agent.farm.Farm;
import agent.farm.Location;


public class ReadParameters implements Reader {

	@Override
	public List<Farm> getFarms() {
		String Line;
		List<Farm> farms = new ArrayList<Farm>();
		ArrayList<String> farmParameters;
		String name = "";
		int age = 0;
		int education = 0;
		int memory = 0;
		double entrepreneurship = 0;
		
		BufferedReader Buffer = null;	
		int index = 0;
		
		List<Graph<String, DefaultEdge>> network = this.getSocialNetworks();   // build social network graphs
		List<Crop> crops = getCropList();
		List<Livestock> livestock = getLivestockList();
		
		try {
			Calendar now = Calendar.getInstance();                             // Gets the current date and time
			int currentYear = now.get(Calendar.YEAR); 
			Buffer = new BufferedReader(new FileReader("./data/farm_data.csv"));
			Line = Buffer.readLine();									       // first line to throw away
			
			while ((Line = Buffer.readLine()) != null) {                       // Read farm's parameters line by line
				farmParameters = CSVtoArrayList(Line);
				Farm farm = new Farm();
				Location location = new Location();							   // create new location for each farm
				List<Product> preferences = new ArrayList<Product>();
				double[] coordinates = {0,0};
				
				name = farmParameters.get(0);
				coordinates[0] = Double.parseDouble(farmParameters.get(1));
				coordinates[1] = Double.parseDouble(farmParameters.get(2));
				location.setCoordinates(coordinates);
				
				farm.setFarmId("Farm" + String.format("%03d", index) );		   // index is used to set the actual farm id value
				farm.setFarmName(name);
				farm.setLocation(location);
				
				farm.setNetwork(network.get(index));

				age = currentYear - Integer.parseInt( farmParameters.get(3));
				education = Integer.parseInt( farmParameters.get(4) );
				memory = Integer.parseInt( farmParameters.get(5));
				entrepreneurship = Double.parseDouble( farmParameters.get(6));
				
				for(int i = 0; i<crops.size(); i++) {
					if (crops.get(i).getName().equals(farmParameters.get(7) )) {
						int ID = crops.get(i).getID();
						Product current_action = new Crop(ID, farmParameters.get(7)); 
						farm.setCurrentAction(current_action);
					}
				}
				
				for(int i = 0; i<livestock.size(); i++) {
					if (livestock.get(i).getName().equals(farmParameters.get(7) )) {
						int ID = livestock.get(i).getID();
						Product current_action = new Livestock(ID, farmParameters.get(7)); 
						farm.setCurrentAction(current_action);
					}
				}
				
				//preferences.clear();
				for (int k = 8; k < farmParameters.size(); k++) {
					for(int i = 0; i<crops.size(); i++) {
						if (crops.get(i).getName().equals(farmParameters.get(k) )) {
							int ID = crops.get(i).getID();
							Product p = new Crop(ID, farmParameters.get(k)); 
							preferences.add(p);
						}
<<<<<<< Updated upstream
					}
				}
				
				for (int k = 8; k < farmParameters.size(); k++) {
					for(int i = 0; i<livestock.size(); i++) {
						if (livestock.get(i).getName().equals(farmParameters.get(k) )) {
							int ID = livestock.get(i).getID();
							Product p = new Livestock(ID, farmParameters.get(k)); 
							preferences.add(p);
=======
						
						for (CropCategory s: CropCategory.values() ) {
							String cat = s.toString();
							if (cat.equalsIgnoreCase(farmParameters.get(i))) {
								p = new Crop(farmParameters.get(i));
								preferences.add(p);
							}
>>>>>>> Stashed changes
						}
					}
				}
				
				Person farmHead = new Person(age, education, memory, entrepreneurship, preferences);          
				
				Random rand = new Random();
				farm.setUncertainty( rand.nextInt(100) );
				farm.setSatisfaction( rand.nextInt(100) );
				
				farm.setAspiration(50);
				farm.setTolerance(entrepreneurship);
				
				List<Double> match = new ArrayList<Double>();
				match.add(1.0);
				farm.setMatch(match);
				
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
	
	/**
	 * Read a csv file that specifies each farm and generate a star network for each listed farm
	 * Each farm id/name is set as the root of the star graph, and each associated node has an associated link weight
	 * Each farm will have an individual graph set based on the master list produced in this method
	 * @return List of graphs for each farm
	 */
	private List<Graph<String, DefaultEdge>> getSocialNetworks(){
		List<Graph<String, DefaultEdge>> NetworkList = new ArrayList<Graph<String, DefaultEdge>>();
		
		BufferedReader Buffer = null;	
		String Line;
		ArrayList<String> data;
		ArrayList<String> FarmNames;
		DefaultEdge edge;
		
		try {
			Buffer = new BufferedReader(new FileReader("./data/social_networks.csv"));
			Line = Buffer.readLine();	
			FarmNames = CSVtoArrayList(Line);
			FarmNames.remove(0);
			
			while ((Line = Buffer.readLine()) != null) {
				data = CSVtoArrayList(Line);
				Graph<String, DefaultEdge> g = new SimpleWeightedGraph<String, DefaultEdge>(DefaultEdge.class);
				
				// build graph with all nodes
				for (int i = 0; i<FarmNames.size(); i++)
				{
					g.addVertex(FarmNames.get(i));
				}
				
				// add all nodes except root to graph as vertices
				for (int i = 0; i<FarmNames.size(); i++)
				{
					if (data.get(0).equalsIgnoreCase(FarmNames.get(i)))
					{
						continue;
					}
					else {
						edge = g.addEdge( data.get(0), FarmNames.get(i) );
						g.setEdgeWeight(edge, Double.parseDouble(data.get(i+1)) );
					}
				}
				NetworkList.add(g);
			}
			Buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return NetworkList;
	}

	/**
	 * Create list of crop type/category from master CSV list
	 * This is used to generate the individual farm product lists
	 * @return List of crops in the master CSV file
	 */
	public List<Crop> getCropList() {
		String Line;
		List<Crop> crops = new ArrayList<Crop>();
		ArrayList<String> cropRow;
		BufferedReader Buffer = null;	

		try {
			Buffer = new BufferedReader(new FileReader("./data/crop_classification.csv"));
			Line = Buffer.readLine();									       // first line to throw away
			
			while ((Line = Buffer.readLine()) != null) {                       // Read crop data
				cropRow = CSVtoArrayList(Line);
				
				int ID = Integer.parseInt(cropRow.get(0));
				String name = cropRow.get(1);
				
				Crop crop = new Crop(ID, name);
				
				crops.add(crop);
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
		return crops;
	}
	
	/**
	 * Create list of livestock type/category from master CSV list
	 * This is used to generate the individual farm product lists
	 * @return List of livestock in the master CSV file
	 */
	public List<Livestock> getLivestockList() {
		String Line;
		List<Livestock> livestock = new ArrayList<Livestock>();
		ArrayList<String> livestockRow;
		BufferedReader Buffer = null;	

		try {
			Buffer = new BufferedReader(new FileReader("./data/livestock_classification.csv"));
			Line = Buffer.readLine();									       // first line to throw away
			
			while ((Line = Buffer.readLine()) != null) {                       // Read livestock data
				livestockRow = CSVtoArrayList(Line);
				
				int ID = Integer.parseInt(livestockRow.get(0));
				String name = livestockRow.get(1);
				
				Livestock stock = new Livestock(ID, name);
				
				livestock.add(stock);
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
		return livestock;
	}
	
	/**
	 * Input a readline from a csv using a split operation 
	 * @param CSV String from input csv file to break into array
	 * @return Result ArrayList of strings 
	 */
	private static ArrayList<String> CSVtoArrayList(String CSV) {		       
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
