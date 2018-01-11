package agent.farm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import product.Crop;
import product.Livestock;
import product.Product;
import reader.FarmProductMatrix;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

/** 
 * Farm object contains all preferences, networks, people, and paramaters associated with each farm
 * @author kellerke
 *
 */
public class Farm implements Member {
	
	private String farmId;
	private String farmName;
	private Person head;
	private Product CurrentAction;
	private Person spouse;
	private Person child;
	private Location location;
	private double Satisfaction;
	private double Aspiration;
	private double Uncertainty;
	private double Tolerance;
	private List<Double> dissimilarity;
	private Graph<String, DefaultEdge> network; 
	private FarmProductMatrix preferences;
	private FarmProductMatrix experience;
	
	/** 
	 * Calculate satisfaction and uncertainty for the decision tree
	 * @return List of Products/Actions that the farm will produce
	 */
	public List<Product> getAction(List<Farm> farms) {
		
		// update satisfaction and uncertainty before making decisions
		updateSatisfaction();
		updateUncertainty(farms);
		// update aspiration levels
		
		// create final action array
		List<Product> products = new ArrayList<Product>();
		products.add(this.CurrentAction);
		
		if ((head.getAge() > 65)) {
			System.out.println(ACTION.EXIT);
			products.add(this.CurrentAction);
		}
		else if (this.Uncertainty >= this.Tolerance) {
			if (this.Satisfaction > this.Aspiration) {
				System.out.println(ACTION.IMITATION);
			}
			else {
				System.out.println(ACTION.OPT_OUT);
			}
		}
		else {
			if (this.Satisfaction > this.Aspiration) {
				System.out.println(ACTION.REPETITION);
			}
			else {
				System.out.println(ACTION.OPTIMIZATION);
			}
		}
		return products;
	}
	
	/**
	 * Using list of all current farms in the system and the social network of the main farm,
	 * update the main farm's uncertainty value based on the social network weight and the dissimilarity 
	 * between neighbor's product types 
	 * 
	 * @param farms Input list of all farms in the system. 
	 */
	private void updateUncertainty(List<Farm> farms) {
        double uncertainty = 0;												   // uncertainty value based on network
        double currentDissimilarity = 0;									   // similarity value of a farm 
        int EdgeCount = 0;													   // how many edges does this farm have (ie neighbors)
		int totalFarms = 0;													   // how many total farms are there in the network
        double sum = 0;														   // sum of previous similarities
        double prevDissimilarityAvg = 0;									   // average of previous similarities
        List<String>  ProductNames = new ArrayList<String>();				   // intermediate variable of just names, not product objects
        Map<String, Integer> ProductMap = new HashMap<String,Integer>();       // map of all products on network, with count of often it's produced
        Double dissimilarity = 0.0;											   // dissimilarity value for farm
        
        Set<DefaultEdge> E;
    		
		E = this.network.outgoingEdgesOf(this.farmName);

        EdgeCount = E.size();
        totalFarms = farms.size();
        
    	for (int j = 0; j < totalFarms; j++)  						           //  loop through all farms
    	{
    		List<Product> p = farms.get(j).getProducts();
    		for (int i = 0; i < p.size(); i++) {
    			if (!ProductNames.contains(p.get(i).getName())) 
    			{
    				ProductNames.add(p.get(i).getName());
    				ProductMap.put(p.get(i).getName(), 1);
    			} else {
    				ProductMap.put(p.get(i).getName(), ProductMap.get(p.get(i).getName()) + 1);    // increment map
    			}
    		}
    	}
    	
    	List<String> mainProduct = new ArrayList<String>();
    	for (int i = 0; i < this.getProducts().size(); i++)
    	{
    		mainProduct.add(this.getProducts().get(i).getName());
    	}
    	
    	// ACTUAL DISSIMILARITY CALULATION
    	for (int i = 0; i < ProductNames.size(); i++)
    	{
    		// if the product is produced by the main farmer ignore that product in the dissimilarity
    		if (mainProduct.contains(ProductNames.get(i))) {
    			continue;
    			
    		} else {
    			// these products are not grown by the main farmer so it counts for the dissimilarity
    			dissimilarity = dissimilarity + (ProductMap.get(ProductNames.get(i)) / ((double)EdgeCount) );
    		}
    	}

        currentDissimilarity = dissimilarity/ProductNames.size();
        
        for (int i = 0; i< this.dissimilarity.size(); i++) {
        	sum = sum + this.dissimilarity.get(i);
        }
        prevDissimilarityAvg = sum / this.dissimilarity.size();                // dissimilarity average over all previous years
        this.dissimilarity.add(currentDissimilarity);                          // add previous match
        
        System.out.println(String.format("Dissimilarity value between Farm %s and Network: %f", farmName, currentDissimilarity));
        uncertainty = (currentDissimilarity - prevDissimilarityAvg)/prevDissimilarityAvg;
        
		setUncertainty(uncertainty);
	}

	/**
	 * Update farm satisfaction level
	 */
	private void updateSatisfaction() {
		double satisfaction = 0;
		double alpha_plus = 0.6;
		double alpha_minus = 0.6;
		double phi_plus = 0.8;
		double phi_minus = 0.8;
		double probability = 0.5;
		double v = 0;
		double theta = 0;
		
		if (this.Satisfaction >= this.Aspiration) {
			v = Math.pow(this.Satisfaction, alpha_plus);
			theta = ( Math.pow(probability, phi_plus) ) / Math.pow( (Math.pow(probability, phi_plus) + Math.pow((1 - probability), phi_plus)), (1/phi_plus) );
		}
		else if (this.Satisfaction < this.Aspiration) {
			v = Math.pow(this.Satisfaction, alpha_minus);
			theta = ( Math.pow(probability, phi_minus) ) / Math.pow( (Math.pow(probability, phi_minus) + Math.pow((1 - probability), phi_minus)), (1/phi_minus) );
		}
		
		satisfaction = v*theta;
		setSatisfaction(satisfaction);
	}
	
	public void setTolerance(double entrepreneurship) {
		Tolerance = entrepreneurship;
	}
	
	public void setUncertainty(double uncertainty) {
		Uncertainty = uncertainty;
	}

	public void setAspiration(double aspiration) {
		Aspiration = aspiration;
	}

	public void setSatisfaction(double satisfaction) {
		Satisfaction = satisfaction;
	}
	
	/** 
	 * Given a new product, what is the cost of switching to that product from the current products
	 * @param newProduct
	 * @param crops
	 * @param livestock
	 * @param farms
	 * @return
	 */
	public double getTransactionCost(String newProduct, List<Crop> crops, List<Livestock> livestock, List<Farm> farms) {
		double dist = 0;
		int i = 0;
		double q;
		int k = 5;
		int time = 0;		
        int totalFarms = 0;													   // how many total farms are there in the network
		Set<DefaultEdge> E;
		Iterator<DefaultEdge> I;
		double S = 0;
        double w;
        double Qvalue;
        double product;
        double max = 0;
        double sum = 0;
        
        double p = 0;
        
		// Tech distance calculation
		for (i = 0; i < this.head.getProducts().size(); i++) {
			dist = dist + getTechDistance( this.head.getProducts().get(i).getName(), newProduct, crops, livestock);
		}
		dist = dist / i; // average distance between the current products and the new product
		
		// personal experience calculation
		time = experience.farmProductValue(this.farmName, newProduct);
		q = 1 / ( 1 +  Math.exp( (-k*time) ));
	
		// social learning calculation
		E = this.network.outgoingEdgesOf(this.farmName);
        totalFarms = farms.size();
        I = E.iterator();
        
        for (i = 0; i < totalFarms; i++) {
        	if (!farms.get(i).getFarmName().equals(this.farmName) ) {
        		w = this.network.getEdgeWeight(I.next());
        		Qvalue = this.experience.farmProductValue(farms.get(i).getFarmName(), newProduct);
        		product = w*Qvalue;
        		if (product > max) {max = product;}
        		sum = sum + product;
        	}
        }
        S = sum/max;
        
        // product preference
        p =  1 - ( this.head.getPreferences().farmProductValue(this.farmName, newProduct) / this.head.getPreferences().getProductName().size() ) ;
        
        double Ej = (q + 0.1*S + 0.1*p);
		double C = dist*(1 - Ej);
		
		return C;
	}
	
	/**
	 * @param p1 product name one
	 * @param p2 product name two
	 * @param crops list of all crops in system
	 * @param livestock list of all livestock in system
	 * @return technological distance between crops
	 */
	public Integer getTechDistance(String p1, String p2, List<Crop> crops, List<Livestock> livestock) {
		int distance = 0;
		List<String> cropName = new ArrayList<String>();
		List<Integer> cropID = new ArrayList<Integer>();
		List<String> liveName = new ArrayList<String>();
		List<Integer> liveID = new ArrayList<Integer>();
		
		// get list of names and ID values to compare
		for (int i = 0; i<crops.size(); i++) {
			cropName.add(crops.get(i).getName());
			cropID.add(crops.get(i).getID());
		}
		
		for (int i = 0; i<livestock.size(); i++) {
			liveName.add(livestock.get(i).getName());
			liveID.add(livestock.get(i).getID());
		}
		
		// if product types are different, return 10
		if (liveName.contains(p1) && !liveName.contains(p2))
		{
			distance = 10;
		}
		else if (cropName.contains(p1) && !cropName.contains(p2))
		{
			distance = 10;
		}
		
		// if both crop or both livestock than check ID values
		else if (cropName.contains(p1) && cropName.contains(p2))
		{
			int index = cropName.indexOf(p1);
			double d1 = cropID.get(index);
			index = cropName.indexOf(p2);
			double d2 = cropID.get(index);
			
			if ( Math.abs(d1 - d2) > 1000.00) {
				distance = 4;
			} else if ( Math.abs(d1 - d2) > 100.00) {
				distance = 3;
			}
			else if ( Math.abs(d1 - d2) > 10.00) {
				distance = 3;
			} else {
				distance = 1;
			}
		}
		
		else if (liveName.contains(p1) && liveName.contains(p2))
		{
			int index = liveName.indexOf(p1);
			double d1 = liveID.get(index);
			index = liveName.indexOf(p2);
			double d2 = liveID.get(index);
			
			if ( Math.abs(d1 - d2) > 1000.00) {
				distance = 4;
			} else if ( Math.abs(d1 - d2) > 100.00) {
				distance = 3;
			}
			else if ( Math.abs(d1 - d2) > 10.00) {
				distance = 3;
			} else {
				distance = 1;
			}
		}
		
		return distance;
	}
		
	
	@Override
	public int getAge() {
		return this.head.getAge();
	}
	@Override
	public int getEducation() {
		return this.head.getEducation();
	}
	@Override
	public FarmProductMatrix getPreferences() {
		
		return this.head.getPreferences();
	}
	@Override
	public int getMemory() {
		return this.head.getMemory();
	}
	public String getFarmId() {
		return farmId;
	}
	public void setFarmId(String farmId) {
		this.farmId = farmId;
	}
	public Person getHead() {
		return head;
	}
	public void setHead(Person farmHead) {
		this.head = farmHead;
	}
	public Person getSpouse() {
		return spouse;
	}
	public void setSpouse(Person spouse) {
		this.spouse = spouse;
	}
	public Person getChild() {
		return child;
	}
	public void setChild(Person child) {
		this.child = child;
	}
	public String getFarmName() {
		return farmName;
	}
	public void setFarmName(String farmName) {
		this.farmName = farmName;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Graph<String, DefaultEdge> getNetwork() {
		return network;
	}
	public void setNetwork(Graph<String, DefaultEdge> network) {
		this.network = network;
	}
	public Product getCurrentAction() {
		return CurrentAction;
	}
	public void setCurrentAction(Product current_action) {
		CurrentAction = current_action;
	}
	public double getTolerance() {
		return Tolerance;
	}
	public double getUncertainty() {
		return Uncertainty;
	}
	public double getAspiration() {
		return Aspiration;
	}
	public double getSatisfaction() {
		return Satisfaction;
	}
	public List<Double> getDissimilarity() {
		return dissimilarity;
	}
	public void setDissimilarity(List<Double> dissimilarity) {
		this.dissimilarity = dissimilarity;
	}
	public void updateMatch(double match)
	{
		this.dissimilarity.add(match);
	}
	@Override
	public List<Product> getProducts() {
		return head.getProducts();
	}

	public void setPreferences(FarmProductMatrix preferences) {
		this.preferences = preferences;
	}

	public FarmProductMatrix getExperience() {
		return this.experience;
	}

	public void setExperience(FarmProductMatrix experience) {
		this.experience = experience;
	}

}
