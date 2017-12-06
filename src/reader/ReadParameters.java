package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import agent.farm.Person;
import product.Crop;
import product.Crop.CropCategory;
import product.Livestock;
import product.Livestock.LivestockCategory;
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
		int entrepreneurship = 0;
		List<Product> preferences = new ArrayList<Product>();
		BufferedReader Buffer = null;	
		int index = 0;
		Product p = null;
		
		List<Graph<String, DefaultEdge>> network = this.getSocialNetworks();   // build social network graphs
		
		try {
			Calendar now = Calendar.getInstance();                             // Gets the current date and time
			int currentYear = now.get(Calendar.YEAR); 
			Buffer = new BufferedReader(new FileReader("./data/farm_data.csv"));
			Line = Buffer.readLine();									       // first line to throw away
			
			while ((Line = Buffer.readLine()) != null) {                       // Read farm's parameters line by line
				farmParameters = CSVtoArrayList(Line);
				System.out.println("ArrayList data: " + farmParameters);
				Farm farm = new Farm();
				Location location = new Location();							   // create new location for each farm
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
				entrepreneurship = Integer.parseInt( farmParameters.get(6));
				
				int len = farmParameters.size();
				preferences.clear();
				for (int i = 7; i < len; i++) {							       // check the element against all possible enum values and add to parameter
					if (farmParameters.get(i) != null) {
						for (LivestockCategory s: LivestockCategory.values() ) {
							String cat = s.toString();
							if (cat.equalsIgnoreCase( farmParameters.get(i))) {
								p = new Livestock(farmParameters.get(i));
								preferences.add(p);
							}
						}
						
						for (CropCategory s: CropCategory.values() ) {
							String cat = s.toString();
							if (cat.equalsIgnoreCase( farmParameters.get(i))) {
								p = new Crop(farmParameters.get(i));
								preferences.add(p);
							}
						}
					}
				}
				
				Person farmHead = new Person(age, education,memory, entrepreneurship, preferences);          
				
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

	private static ArrayList<String> CSVtoArrayList(String CSV) {		       // Utility which converts CSV to ArrayList using Split Operation
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
