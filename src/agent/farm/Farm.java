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
import reader.Parameters;

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
	private List<Double> IncomeHistory;
	private double Aspiration;
	private double Uncertainty;
	private double Tolerance;
	private Graph<String, DefaultEdge> network; 
	private FarmProductMatrix experience;
	private FarmProductMatrix preferences;
	private List<Product> currentProducts;
	private List<Crop> crops;
	private List<Livestock> livestock;
	private Parameters parameters;
	
	/** 
	 * update satisfaction and uncertainty for the farm
	 * create product selection calculator for this farm
	 * check which option farm will pursue
	 * return decision from calculator and update product list
	 * 
	 * @param farms full list of all farms in system
	 * @param income of this particular farm
	 * @return List of Products/Actions that the farm will produce
	 */
	public List<String> getUpdatedActions(List<Farm> farms, double income) {
	    List<String> products = new ArrayList<String>();								 // list of names of products to return
	    ProductSelectionCalculator cal = new ProductSelectionCalculator(this, farms);    // calculator for the product selection
		List<Product> current = new ArrayList<Product>();							     // current products (objects - not names) in system 

	    updateSatisfaction(income);
		updateUncertainty(farms);
		updateAspiration();
		updateTolerance();                                                     // currently does nothing
		
		if ((head.getAge() > 65)) {
			System.out.println("EXIT");
		}
		else if (this.Uncertainty >= this.Tolerance) {
			if (this.Satisfaction >= 0) {
				System.out.println("IMITATION");
				// check calculator with S,Q,P
				products = cal.getImitationProducts();
			}
			else {
				System.out.println("OPT_OUT");
			}
		}
		else {
			if (this.Satisfaction >= 0) {
				System.out.println("REPETITION");
				
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
		
		for (int k = 0; k < products.size(); k++) {
			for(int i = 0; i<crops.size(); i++) {
				if (crops.get(i).getName().equals(products.get(k) )) {
					int ID = crops.get(i).getID();
					Product p = new Crop(ID, products.get(k)); 
					current.add(p);
				}
			}
		}
				
		for (int k = 0; k < products.size(); k++) {
			for(int i = 0; i<livestock.size(); i++) {
				if (livestock.get(i).getName().equals(products.get(k) )) {
					int ID = livestock.get(i).getID();
					Product p = new Livestock(ID, products.get(k)); 
					current.add(p);
				}
			}
		}	
		
		this.setCurrentProducts(current);

		return products;
	}
	
	// update functions for farm parameters
	/**
	 * Using list of all current farms in the system and the social network of the main farm,
	 * update the main farm's uncertainty value based on the social network weight and the dissimilarity 
	 * between neighbor's product types 
	 * 
	 * @param farms Input list of all farms in the system. 
	 */
	private void updateUncertainty(List<Farm> farms) {
        double currentDissimilarity = 0;									   // similarity value of a farm 
        int EdgeCount = 0;													   // how many edges does this farm have (ie neighbors)
		int totalFarms = 0;													   // how many total farms are there in the network
        List<String>  ProductNames = new ArrayList<String>();				   // intermediate variable of just names, not product objects
        Map<String, Integer> ProductMap = new HashMap<String,Integer>();       // map of all products on network, with count of often it's produced
        Double dissimilarity = 0.0;											   // dissimilarity value for farm
        Set<DefaultEdge> E;                                                    // set of edges in network with this farm at the head
    		
		E = this.network.outgoingEdgesOf(this.farmName);
        EdgeCount = E.size();
        totalFarms = farms.size();
        
    	for (int j = 0; j < totalFarms; j++)  						           //  loop through all farms and check if they have a connection to main farm
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
        
		setUncertainty(currentDissimilarity);
	}
     /**
	 * Based on the current income level of the farmer calculate new satisfaction level.
	 * The farmer's income is set externally from farmdyn 
	 */
	private void updateSatisfaction(double income) {
		this.updateIncomeHistory(income);									   // add income at current time step to history log, remove oldest income
		
		double current_satisfaction = currentSatisfaction();
		setSatisfaction(current_satisfaction);                                                     // uses updated income history
	}
	/** 
	 * Based on the historical income data, calculate the current aspiration level
	 */
	private void updateAspiration() {
		double aspiration = 0;
		double alpha = this.parameters.getA();
		
		aspiration = alpha*mean(IncomeHistory);
		
		setAspiration(aspiration);
	}
	/** 
	 * Based on the historical income data, calculate the current aspiration level
	 */
	private void updateTolerance() {
		// do nothing - tolerance set externally
	}
	/** 
	 * Each time period, t, call this function to increment the experience vector of this farm. 
	 * This experience vector is part of a shared experience matrix that all farms have
	 * If the farm is currently farming a product, then increase the experience of that product for that farm.
	 * For all other possible products in the experience vector for this farm, decrement the experience by one year.
	 */
	public void updateExperience() {
		List<String> productNames = new ArrayList<String>();				   // array of names of products for comparison
		
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
			
			if(value > this.getMemory()) value = this.getMemory();
			if(value < 0) value = 0;
			
			this.experience.setFarmProductValue(farmName, this.experience.getProductName().get(i), value);
			
		}	
	}
	/**
	 * Update income history by removing oldest income and replacing with new income
	 * @param income
	 */
	public void updateIncomeHistory (double income) {
		List<Double> temp = new ArrayList<Double>();                           // update array for new incomes
		temp.add(income);
		
		for (int i = 0; i< this.getMemory() - 1; i++) {
			temp.add(this.IncomeHistory.get(i));
		}
			
		setIncomeHistory(temp); 
	}
	
	// helper functions
	/** 
	 * Calculate satisfaction score given income value
	 * @param income of farmer
	 * @return satisfaction
	 */
	private double calculateSatisfaction(double income) {
		double satisfaction = 0;
		
		double alpha_plus = this.parameters.getAlpha_plus();
		double alpha_minus = this.parameters.getAlpha_minus();
		double phi_plus = this.parameters.getPhi_plus();
		double phi_minus = this.parameters.getPhi_minus();
		
		double probability = 0.5;
		double lambda = this.parameters.getLambda();
		double v = 0;
		double theta = 0; 
		
		if (income >= this.Aspiration) {
			v = Math.pow(income, alpha_plus);
			theta = ( Math.pow(probability, phi_plus) ) / Math.pow( (Math.pow(probability, phi_plus) + Math.pow((1 - probability), phi_plus)), (1/phi_plus) );
		}
		else if (income < this.Aspiration) {
			v = (-1)*Math.pow(income, alpha_minus);
			theta = ( Math.pow(probability, phi_minus) ) / Math.pow( (Math.pow(probability, phi_minus) + Math.pow((1 - probability), phi_minus)), (1/phi_minus) );
		}

		satisfaction = v*theta*lambda;
		
		return satisfaction;
	}
	/**
	 * From the farm income history, calculate current satisfaction level as the average of historical satisfaction
	 * @return mean historical satisfaction
	 */
	private double currentSatisfaction() {
		List<Double> sat = new ArrayList<Double>();
		
		for (int i = 0; i< this.getMemory(); i++) {
			sat.add(calculateSatisfaction(this.IncomeHistory.get(i)));
		}
		return mean(sat);
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

	// getters and setters for all fields
	public void setAspiration(double aspiration) {
		Aspiration = aspiration;
	}
	public void setUncertainty(double uncertainty) {
		Uncertainty = uncertainty;
	}
	public void setTolerance(double entrepreneurship) {
		Tolerance = entrepreneurship;
	}
	public void setSatisfaction(double satisfaction) {
		Satisfaction = satisfaction;
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
	public List<Double> getIncomeHistory() {
		return IncomeHistory;
	}
	public void setIncomeHistory(List<Double> incomeHistory) {
		IncomeHistory = incomeHistory;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
}



