package agent.farm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import product.Crop;
import product.Livestock;
import product.Product;
import productselection_calculator.ProductSelectionCalculator;
import reader.FarmProductMatrix;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

/** 
 * Farm object contains people and parameters associated with each farm
 * contains copy of crop and livestock list, preferences and experiences and network list
 * 
 * @author kellerke
 *
 */
public class Farm {
	private String farmName;
	private Person head;
	private Location location;
	private double Satisfaction;
	private List<Double> PreviousSatisfaction;
	private double Aspiration;
	private double Uncertainty;
	private double Tolerance;
	private List<Double> dissimilarity;
	private Graph<String, DefaultEdge> network; 
	private FarmProductMatrix experience;
	private FarmProductMatrix preferences;
	private List<Product> currentProducts;
	private List<Crop> crops;
	private List<Livestock> livestock;
	
	/** 
	 * update satisfaction and uncertainty for the farm
	 * check which option farm will pursue
	 * return decision, and update product list
	 * 
	 * @return List of Products/Actions that the farm will produce
	 */
	public List<String> getAction(List<Farm> farms, double income) {
	    List<String> products = new ArrayList<String>();
	    ProductSelectionCalculator cal = new ProductSelectionCalculator(this, farms);
		
	    updateSatisfaction(income);
		updateUncertainty(farms);
		
		if ((head.getAge() > 65)) {
			System.out.println("EXIT");
		}
		else if (this.Uncertainty >= this.Tolerance) {
			if (this.Satisfaction >= 1) {
				System.out.println("IMITATION");
				// check calculator with S,Q,P
				products = cal.getImitationProducts();
			}
			else {
				System.out.println("OPT_OUT");
			}
		}
		else {
			if (this.Satisfaction >= 1) {
				System.out.println("REPETITION");
				//products = this.getCurrentProducts();
				
				for (int i = 0; i < this.getCurrentProducts().size(); i++) {
					products.add(this.getCurrentProducts().get(i).getName());
				}
				
			}
			else {
				System.out.println("OPTIMIZATION");
				// check calculator with Q,P (no social costs)
				products = cal.getOptimizeProducts();
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
    		List<Product> p = farms.get(j).getCurrentProducts();
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
    	for (int i = 0; i < this.getCurrentProducts().size(); i++)
    	{
    		mainProduct.add(this.getCurrentProducts().get(i).getName());
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
        
        uncertainty = (currentDissimilarity - prevDissimilarityAvg)/prevDissimilarityAvg;
        //System.out.println(String.format("Dissimilarity value between %s and Network: %f, %f, %f", farmName, currentDissimilarity, uncertainty, prevDissimilarityAvg));
        
        uncertainty = (currentDissimilarity);
        
		setUncertainty(uncertainty);
	}

	/**
	 * Based on the current income level of the farmer calculate new satisfaction level.
	 * The farmer's income is set externally from farmdyn 
	 */
	private void updateSatisfaction(double income) {
		double satisfaction = calculateSatisfaction(income);
		
		this.updatePreviousSatisfaction(satisfaction);
		
		setSatisfaction();
	}
	
	/** 
	 * Calculate satisfaction score given income value
	 * @param income of farmer
	 * @return satisfaction
	 */
	private double calculateSatisfaction(double income) {
		double satisfaction = 0;
		
		double alpha_plus = 0.6;
		double alpha_minus = 0.65;
		double phi_plus = 0.8;
		double phi_minus = 0.8;
		
		double probability = 0.5;
		double lambda = 0;
		double v = 0;
		double theta = 0; 
		
		if (income >= this.Aspiration) {
			v = Math.pow(income, alpha_plus);
			theta = ( Math.pow(probability, phi_plus) ) / Math.pow( (Math.pow(probability, phi_plus) + Math.pow((1 - probability), phi_plus)), (1/phi_plus) );
			lambda = 1;
		}
		else if (income < this.Aspiration) {
			v = Math.pow(income, alpha_minus);
			theta = ( Math.pow(probability, phi_minus) ) / Math.pow( (Math.pow(probability, phi_minus) + Math.pow((1 - probability), phi_minus)), (1/phi_minus) );
			lambda = -1;
		}

		satisfaction = v*theta*lambda;
		
		return satisfaction;
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

	public void setSatisfaction() {
	
		Satisfaction = mean(this.PreviousSatisfaction);
	}
	
	/** 
	 * Return mean value of provided list 
	 * @param list of values to calculate mean with
	 * @return mean
	 */
	private double mean(List<Double> list) {
		double mean = 0;
		
		for (int i = 0; i<list.size(); i++) {
			mean = mean + list.get(i);
		}
		
		return mean / list.size();
	}
	
	/** 
	 * Each time period, t, call this function to increment the experience vector of this farm. 
	 * This experience vector is part of a shared experience matrix that all farms have
	 */
	public void updateExperience() {
		List<String> productNames = new ArrayList<String>();
		
		for (int i = 0; i<this.getCurrentProducts().size(); i++) {
			productNames.add(this.getCurrentProducts().get(i).getName());
		}
		
		for (int i = 0; i< this.experience.getProductName().size(); i++ ) {
			
			int value = this.experience.getFarmProductValue(farmName, this.experience.getProductName().get(i));
			
			if (productNames.contains(this.experience.getProductName().get(i))) {
				value += 1;
			}
			else {
				value -=1;
			}
			
			if(value < 0) value = 0;
			
			this.experience.setFarmProductValue(farmName, this.experience.getProductName().get(i), value);
			
		}
		
	}
	
	public int getAge() {
		return this.head.getAge();
	}
	public int getEducation() {
		return this.head.getEducation();
	}
	public FarmProductMatrix getPreferences() {
		
		return this.preferences;
	}
	public int getMemory() {
		return this.head.getMemory();
	}
	public Person getHead() {
		return head;
	}
	public void setHead(Person farmHead) {
		this.head = farmHead;
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
	public FarmProductMatrix getExperience() {
		return this.experience;
	}
	public void setExperience(FarmProductMatrix experience) {
		this.experience = experience;
	}

	public void setPreferences(FarmProductMatrix preferences) {
		this.preferences = preferences;
	}

	public void setCurrentProducts(List<Product> products) {
		this.currentProducts = products;
	}
	public List<Product> getCurrentProducts() {
		return this.currentProducts;
	}
	public List<Livestock> getLivestock() {
		return livestock;
	}
	public void setLivestock(List<Livestock> livestock) {
		this.livestock = livestock;
	}
	public List<Crop> getCrops() {
		return crops;
	}
	public void setCrops(List<Crop> crops) {
		this.crops = crops;
	}

	public List<Double> getPreviousSatisfaction() {
		return PreviousSatisfaction;
	}

	public void setInitialSatisfaction(List<Double> previousSatisfaction) {
		for (int i = 0; i< previousSatisfaction.size(); i++) {
			previousSatisfaction.set(i, calculateSatisfaction(previousSatisfaction.get(i)));
		}
		
		PreviousSatisfaction = previousSatisfaction;
	}
	
	public void setPreviousSatisfaction(List<Double> previousSatisfaction) {

		PreviousSatisfaction = previousSatisfaction;
	}
	
	public void updatePreviousSatisfaction(Double sat) {
		List<Double> temp = new ArrayList<Double>();
		temp.add(sat);
		
		for (int i = 1; i<this.getMemory(); i++) {
			temp.add(PreviousSatisfaction.get(i-1));
		}
		
		setPreviousSatisfaction(temp);
	}
}



