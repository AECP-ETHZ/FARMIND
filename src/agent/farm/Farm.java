package agent.farm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import product.Product;
import reader.FarmProductMatrix;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

/** 
 * Farm object contains all preferences, networks, people, and parameters associated with each farm
 * @author kellerke
 *
 */
public class Farm implements Member {
	
	private String farmId;
	private String farmName;
	private Person head;
	//private Product CurrentAction;
	private Person spouse;
	private Person child;
	private Location location;
	private double Satisfaction;
	private double Aspiration;
	private double Uncertainty;
	private double Tolerance;
	private List<Double> dissimilarity;
	private Graph<String, DefaultEdge> network; 
	private FarmProductMatrix experience;
	
	/** 
	 * Calculate satisfaction and uncertainty for the decision tree
	 * @return List of Products/Actions that the farm will produce
	 */
	public List<Product> getAction(List<Farm> farms) {
		
		// update satisfaction and uncertainty before making decisions
		updateSatisfaction(100.00);
		updateUncertainty(farms);
		
		// create final action array
		List<Product> products = new ArrayList<Product>();
		
		if ((head.getAge() > 65)) {
			System.out.println(ACTION.EXIT);
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
	 * Based on the current income level of the farmer calculate new satisfaction level.
	 * The farmer's income is set externally from farmdyn 
	 */
	private void updateSatisfaction(double income) {
		double satisfaction = 0;
		double alpha_plus = 0.6;
		double alpha_minus = 0.6;
		double phi_plus = 0.8;
		double phi_minus = 0.8;
		double probability = 0.5;
		double v = 0;
		double theta = 0;
		
		if (income >= this.Aspiration) {
			v = Math.pow(income, alpha_plus);
			theta = ( Math.pow(probability, phi_plus) ) / Math.pow( (Math.pow(probability, phi_plus) + Math.pow((1 - probability), phi_plus)), (1/phi_plus) );
		}
		else if (income < this.Aspiration) {
			v = Math.pow(income, alpha_minus);
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

	public FarmProductMatrix getExperience() {
		return this.experience;
	}

	public void setExperience(FarmProductMatrix experience) {
		this.experience = experience;
	}

}
