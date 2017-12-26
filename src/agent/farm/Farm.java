package agent.farm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import product.Product;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class Farm implements Member {
	
	private String farmId;
	private String farmName;
	private Person head;
	private Product CurrentAction;
	private Person spouse;
	private Person child;
	private Location location; 
	private Graph<String, DefaultEdge> network; 
	private double Satisfaction;
	private double Aspiration;
	private double Uncertainty;
	private double Tolerance;
	private List<Double> similarity;


	/** 
	 * Calculate satisfaction and uncertainty for the decision tree
	 * @return List of Products/Actions that the farm will produce
	 */
	public List<Product> getAction(List<Farm> farms) {
		
		// update satisfaction and uncertainty before making decisions
		updateSatisfaction();
		updateUncertainty(farms);
		
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
	 * update the main farm's uncertainty value based on the social network weight and the similarity 
	 * between neighbor's product types 
	 * 
	 * @param farms Input list of all farms in the system. 
	 */
	private void updateUncertainty(List<Farm> farms) {
        double uncertainty = 0;												   // uncertainty value based on network
        double currentSimilarity = 0;										   // similarity value of a farm 
        double weight = 0;													   // social weight between two farms
        double matchingProducts = 0;										   // how many products match between farms
		int EdgeCount = 0;													   // how many edges does this farm have (ie neighbors)
		int totalFarms = 0;													   // how many total farms are there in the network
		double mainFarmProductCount = 0;									   // how many products the main farm produces
		double neighborProductCount = 0;								       // how many products the neighbor farm produces
		double farmSetProductCount = 0;										   // total product count between the two farms
        double sum = 0;														   // sum of previous similarities
        double prevSimilarityAvg = 0;										   // average of previous similarities
        List<String>  ProductNames = new ArrayList<String>();				   // intermediate variable of just names, not product objects
        Map<String, Integer> ProductMap = new HashMap<String,Integer>();       // map of all products on network, with count of often it's produced

        
        Set<DefaultEdge> E;
        Iterator<DefaultEdge> I;
    		
		E = this.network.outgoingEdgesOf(this.farmName);
		Object[] neighbors = this.network.vertexSet().toArray();
		
        I = E.iterator();
        EdgeCount = E.size();
        totalFarms = farms.size();
        
        mainFarmProductCount = this.getPreferences().size();
        
        
    	for (int j = 0; j < totalFarms; j++) 						       //  loop through all farms
    	{
    		List<Product> p = farms.get(j).getPreferences();
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
    	
    	for (int i = 0; i < this.getPreferences().size(); i++)
    	{
    		mainProduct.add(this.getPreferences().get(i).getName());
    	}
    	
    	Double dissimilarity = 0.0;
    	
    	for (int i = 0; i < ProductNames.size(); i++)
    	{
    		if (mainProduct.contains(ProductNames.get(i))) {
    			continue;
    			
    		} else {
    			dissimilarity = dissimilarity + (ProductMap.get(ProductNames.get(i)) / ((double)EdgeCount) );
    		}
    	}
    	
    	dissimilarity = dissimilarity/ProductNames.size();
    	
    	// THIS IS THE OLD VERSION
        for (int i = 0; i<= EdgeCount; i++)									   // loop through all neighbors in the graph			
        {
        	for (int j = 0; j < totalFarms; j++) 						       //  loop through all farms
        	{
        		if (farms.get(j).farmName.equals(neighbors[i].toString() ) && !farms.get(j).farmName.equals(this.farmName)) {
        			List<Product> p = farms.get(j).getPreferences();		   // product of neighbor farms
        			neighborProductCount = p.size();
        			farmSetProductCount = mainFarmProductCount + neighborProductCount;
        			
        			weight = network.getEdgeWeight(I.next());
        			
        			matchingProducts = 0;								    
        			for (int k = 0; k < mainFarmProductCount; k++)             // get number of matching products between two farms
        			{
        				for (int m = 0; m < neighborProductCount; m++)
        				{
        					if ( this.getPreferences().get(k).getName().equals(p.get(m).getName()) ) {
        						matchingProducts++;
        					}
        				}
        			}
        			currentSimilarity = currentSimilarity + weight * (matchingProducts/farmSetProductCount);
        		}
        	}
        } 
        // END OLD VERSION
        
        // TEMPORARY SETUP
        currentSimilarity = dissimilarity; 
        
        for (int i = 0; i< this.similarity.size(); i++) {
        	sum = sum + this.similarity.get(i);
        }
        prevSimilarityAvg = sum / this.similarity.size(); // match average
        this.similarity.add(currentSimilarity);        // add previous match
        
        System.out.println(String.format("Similarity value between Current Farm and Network: %f", currentSimilarity));
        uncertainty = (currentSimilarity - prevSimilarityAvg)/prevSimilarityAvg;
        
		setUncertainty(uncertainty);
	}

	/**
	 * Update farm satisfaction level
	 */
	private void updateSatisfaction() {
		// setSatisfaction();
	}
	
	public void setTolerance(double entrepreneurship) {
		Tolerance = entrepreneurship;
	}
	
	public void setUncertainty(double uncertainty2) {
		Uncertainty = uncertainty2;
	}

	public void setAspiration(double aspiration) {
		Aspiration = aspiration;
	}

	public void setSatisfaction(double satisfaction) {
		Satisfaction = satisfaction;
	}
	
	@Override
	public int getAge() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getEducation() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List<Product> getPreferences() {
		
		return this.head.getPreferences();
	}
	@Override
	public int getMemory() {
		// TODO Auto-generated method stub
		return 0;
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

	public List<Double> getMatch() {
		return similarity;
	}

	public void setMatch(List<Double> match) {
		this.similarity = match;
	}
	
	public void updateMatch(double match)
	{
		this.similarity.add(match);
	}

}
