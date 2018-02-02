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
import product.Livestock;
import product.Product;
import agent.farm.Farm;
import agent.farm.Location;


public class ReadParameters implements Reader {

	public static final int NAME = 0;
	public static final int COORDINATE1 = 1;
	public static final int COORDINATE2 = 2;
	public static final int AGE = 3;
	public static final int EDUCATION = 4;
	public static final int MEMORY = 5;
	public static final int ENTREPRENEURSHIP = 6;
	public static final int INCOME_INDEX = 10;
	public static final int START_ACTION_INDEX = 7;					       // the input spreadsheet starts the actions at column 12

	@Override
	public List<Farm> getFarms(int parameterSet) {
		String Line;
		List<Farm> farms = new ArrayList<Farm>();
		ArrayList<String> farmParameters;
		String name = "";
		int age = 0;
		int education = 0;
		int memory = 0;
		double entrepreneurship = 0;
		BufferedReader Buffer = null;	 									   // read input file
		int farm_count_index = 0;                                              // index is used to set the actual farm id value
		
		// reference objects for building farm list
		// each farm has a link to the reference object. This allows each farm to update the shared data objects
		List<Graph<String, DefaultEdge>> network = this.getSocialNetworks();   
		List<Crop> crops = getCropList();
		List<Livestock> livestock = getLivestockList();
		FarmProductMatrix pref = getPreferences();
		FarmProductMatrix experience = getExperience();
		Parameters parameters = getParameters(parameterSet);
		
		try {
			Calendar now = Calendar.getInstance();                             // Gets the current date and time
			int currentYear = now.get(Calendar.YEAR); 
			Buffer = new BufferedReader(new FileReader("./data/farm_data.csv"));
			Line = Buffer.readLine();									       // first line to throw away
			
			while ((Line = Buffer.readLine()) != null) {                       // Read farm's parameters line by line
				farmParameters = CSVtoArrayList(Line);
				Farm farm = new Farm();
				Location location = new Location();							   // create new location for each farm
				List<Product> currentProducts = new ArrayList<Product>();
				List<Double> income = new ArrayList<Double>();
				double[] coordinates = {0,0};
				
				name = farmParameters.get(NAME);
				coordinates[0] = Double.parseDouble(farmParameters.get(COORDINATE1));
				coordinates[1] = Double.parseDouble(farmParameters.get(COORDINATE2));
				location.setCoordinates(coordinates);
				   
				farm.setFarmName(name);
				farm.setLocation(location);
				farm.setNetwork(network.get(farm_count_index));

				age = currentYear - Integer.parseInt( farmParameters.get(AGE));
				education = Integer.parseInt( farmParameters.get(EDUCATION) );
				memory = Integer.parseInt( farmParameters.get(MEMORY));
				entrepreneurship = Double.parseDouble( farmParameters.get(ENTREPRENEURSHIP));
				Person farmHead = new Person(age, education, memory, entrepreneurship);          

				currentProducts.clear();
				for (int k = START_ACTION_INDEX; k < farmParameters.size(); k++) {
					for(int i = 0; i<crops.size(); i++) {
						if (crops.get(i).getName().equals(farmParameters.get(k) )) {
							int ID = crops.get(i).getID();
							Product p = new Crop(ID, farmParameters.get(k)); 
							currentProducts.add(p);
						}
					}
				}
						
				for (int k = START_ACTION_INDEX; k < farmParameters.size(); k++) {
					for(int i = 0; i<livestock.size(); i++) {
						if (livestock.get(i).getName().equals(farmParameters.get(k) )) {
							int ID = livestock.get(i).getID();
							Product p = new Livestock(ID, farmParameters.get(k)); 
							currentProducts.add(p);
						}
					}
				}	
				
				for (int i = 0; i < memory; i++) {
					income.add( Double.parseDouble( farmParameters.get(i+INCOME_INDEX) ) );
				}
				
				List<Double> avgIncome = new ArrayList<Double>(income);
				avgIncome.remove(0);                                           // remove first element
				double personalIncomeAverage = mean(avgIncome);
				farm.setIncomeHistory(income);
				farm.setLastYearPersonalIncomeAverage(personalIncomeAverage);
				farm.setExperience(experience);
				farm.setPreferences(pref);
				farm.setLivestock(livestock);
				farm.setCrops(crops);
				farm.setTolerance(entrepreneurship);
				farm.setCurrentProducts(currentProducts);
				farm.setHead(farmHead);
				farm.setParameters(parameters);
				
				farms.add(farm);
				farm_count_index++;	
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

	private Parameters getParameters(int parameterSet) {
		String Line;
		ArrayList<String> matrixRow;
		BufferedReader Buffer = null;	
		Parameters parameters = new Parameters();

		try {
			Buffer = new BufferedReader(new FileReader("./data/parameters.csv"));
			Line = Buffer.readLine();
			
			for(int i = 0; i < parameterSet; i++) {
				Line = Buffer.readLine();
			}
			
			matrixRow = CSVtoArrayList(Line);
			parameters.setAlpha_plus(Double.parseDouble(matrixRow.get(1)) );
			parameters.setAlpha_minus(Double.parseDouble(matrixRow.get(2)) );
			
			parameters.setLambda(Double.parseDouble(matrixRow.get(3)) );
			
			parameters.setPhi_plus(Double.parseDouble(matrixRow.get(4)) ); 
			parameters.setPhi_minus(Double.parseDouble(matrixRow.get(5)) ); 
			
			parameters.setA(Double.parseDouble(matrixRow.get(6)) ); 
			parameters.setB(Double.parseDouble(matrixRow.get(7)) ); 
			parameters.setK(Double.parseDouble(matrixRow.get(8)) ); 
			parameters.setName(matrixRow.get(9));
				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (Buffer != null) Buffer.close();
			} catch (IOException Exception) {
				Exception.printStackTrace();
			}
		}
		
		return parameters;
	}

	/**
	 * Read preferences of each farm for each crop and build preference object
	 * @return
	 */
	public FarmProductMatrix getPreferences() {
		String Line;
		ArrayList<String> matrixRow;
		BufferedReader Buffer = null;	
		FarmProductMatrix preferences = new FarmProductMatrix();

		try {
			Buffer = new BufferedReader(new FileReader("./data/products_preference.csv"));
			Line = Buffer.readLine();
			matrixRow = CSVtoArrayList(Line);
			matrixRow.remove(0);
			preferences.setProductName(matrixRow);
			
			while ((Line = Buffer.readLine()) != null) {                       // Read row data
				matrixRow = CSVtoArrayList(Line);
				preferences.setProductMap(matrixRow);
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
		
		return preferences;
	}
	
	public FarmProductMatrix getExperience() {
		String Line;
		ArrayList<String> matrixRow;
		BufferedReader Buffer = null;	
		FarmProductMatrix experience = new FarmProductMatrix();

		try {
			Buffer = new BufferedReader(new FileReader("./data/farming_years.csv"));
			Line = Buffer.readLine();
			matrixRow = CSVtoArrayList(Line);
			matrixRow.remove(0);
			experience.setProductName(matrixRow);
			
			while ((Line = Buffer.readLine()) != null) {                       // Read row data
				matrixRow = CSVtoArrayList(Line);
				experience.setProductMap(matrixRow);
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
		
		return experience;
		
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
	
	/** 
	 * Return mean value of provided list 
	 * @param list of values to calculate mean with
	 * @return mean
	 */
	private double mean(List<Double> list) {
		double mean = 0;												       // mean value to return
		
		for (int i = 0; i<list.size(); i++) {
			mean = mean + list.get(i);
		}
		
		return mean / list.size();
	}

}
