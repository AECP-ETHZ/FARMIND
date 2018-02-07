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
	private double IncomeUncertainty;
	private double Tolerance;
	private Graph<String, DefaultEdge> network; 
	private FarmProductMatrix experience;
	private FarmProductMatrix preferences;
	private List<Product> currentProducts;
	private List<Crop> crops;
	private List<Livestock> livestock;
	private Parameters parameters;
	private int strategy;
	private double incomeProbability;
	private double regionIncomeChangePercent;
	private double lastYearPersonalIncomeAverage;
	
	/** 
	 * Update all parameters of the farm data
	 * @param allFarms list of all the input farms
	 * @param income input value of farm
	 * @param probability of an income occurring in our distribution
	 */
	public void updateFarmData(List<Farm> allFarms, double income, double probability) {
		updateIncomeHistoryList(income);									       // for year = 1, we pass in -1 for income so we don't update the income
	    updateIncomeAverage();
	    setIncomeProbability(probability);

	    updateIncomeUncertainty();
	    updateAspiration();
	    updateSatisfaction();									
		updateUncertainty(allFarms);
		updateTolerance();      
	}
	/** 
	 * 1. create product selection calculator for this farm
	 * 2. based on consumat model, decide which of the five decisions the farm will pursue
	 * 
	 * Fuzzy logic is used for optimization and imitation decisions to select a set of products to pursue. 
	 * 
	 * For the sensitivity testing phase, we need to 'fake' a linear programming model so we select half of the set to return as our final decision. 
	 * We keep track of both sets for use during the sensitivity analysis
	 * 
	 * @param allFarms full list of all farms in system
	 * @param income of this particular farm
	 * @return List containing the 1) full fuzzy logic selection and 2) the minimum list to imitate the LP simulator selection
	 */
	public List<List<String>> makeDecision(List<Farm> allFarms) {
	    List<String> fullProductSet = new ArrayList<String>();						     // list of names of products from fuzzy logic
	    List<String> minProductSet = new ArrayList<String>();						     // list of names of products to return to mimic LP
		List<Product> current = new ArrayList<Product>();							     // current products (objects - not names) in system 
		ProductSelectionCalculator cal = new ProductSelectionCalculator(this, allFarms); // calculator for the product selection
		double small_set = 0;

		if ((head.getAge() > 650)) {
			//System.out.println("EXIT");
			this.strategy = 1;
		}
		else if (this.Uncertainty >= this.Tolerance) {
			if (this.Satisfaction >= 0) {
				//System.out.println("IMITATION");
				this.strategy = 2;
				fullProductSet = cal.getImitationProducts();
				
				small_set = Math.round( (double)fullProductSet.size()/2.0) ;
				while(small_set > 0) {
					minProductSet.add(fullProductSet.get((int) (fullProductSet.size() - small_set--)));		   // last element in fullSet is the highest rated product
				}
			}
			else {
				//System.out.println("EXIT");
				this.strategy = 1;
			}
		}
		else {
			if (this.Satisfaction >= 0) {
				//System.out.println("REPETITION");
				this.strategy = 4;
				for (int i = 0; i < this.getCurrentProducts().size(); i++) {
					minProductSet.add(this.getCurrentProducts().get(i).getName());
				} 
			}
			else {
				//System.out.println("OPTIMIZATION");
				this.strategy = 3;
				fullProductSet = cal.getOptimizeProducts();
				small_set = Math.round( (double)fullProductSet.size()/2.0) ;
				while(small_set > 0) {
					minProductSet.add(fullProductSet.get((int) (fullProductSet.size() - small_set--)));		   // last element in fullSet is the highest rated product
				}
			}
		}

		// this is to build a list of product objects - not just names
		for (int k = 0; k < minProductSet.size(); k++) {
			for(int i = 0; i<crops.size(); i++) {
				if (crops.get(i).getName().equals(minProductSet.get(k) )) {
					int ID = crops.get(i).getID();
					Product p = new Crop(ID, minProductSet.get(k)); 
					current.add(p);
				}
			}
		}	
		for (int k = 0; k < minProductSet.size(); k++) {
			for(int i = 0; i<livestock.size(); i++) {
				if (livestock.get(i).getName().equals(minProductSet.get(k) )) {
					int ID = livestock.get(i).getID();
					Product p = new Livestock(ID, minProductSet.get(k)); 
					current.add(p);
				}
			}
		}	
		
		this.setCurrentProducts(current);                                      // update current products for the farm instance
		
		List<List<String>> ret = new ArrayList<List<String>>();				   // list of both sets to return for sensitivity analysis
		ret.add(fullProductSet);
		ret.add(minProductSet);
		
		return ret;
	}
	
	// update functions for farm parameters
	/**
	 * Using list of all current farms in the system and the social network of the main farm,
	 * update the main farm's uncertainty value based on the social network weight and the dissimilarity 
	 * between neighbor's product types 
	 * 
	 * @param farms Input list of all farms in the system. 
	 */
	public void updateUncertainty(List<Farm> farms) {
        double currentDissimilarity = 0;									   // similarity value of a farm 
        int EdgeCount = 0;													   // how many edges does this farm have (ie neighbors)
		int totalFarms = 0;													   // how many total farms are there in the network
        List<String>  networkProductList = new ArrayList<String>();			   // intermediate variable of just names, not product objects
        Map<String, Integer> ProductMap = new HashMap<String,Integer>();       // map of all products on network, with count of often it's produced
        Double dissimilarity = 0.0;											   // dissimilarity value for farm
        Set<DefaultEdge> E;                                                    // set of edges in network with this farm at the head
    		
		E = this.network.outgoingEdgesOf(this.farmName);
        totalFarms = farms.size();
        
		Iterator<DefaultEdge> I;											   // iterator through all edges
        I = E.iterator();
        double w;
        
        for (int k = 0; k < totalFarms; k++) {
        	if (!farms.get(k).getFarmName().equals(this.getFarmName()) ) {
        		w = this.getNetwork().getEdgeWeight(I.next());						   // weight of social tie between main farm and farm i
        		if (w > 0) {
        			EdgeCount++;
            		List<Product> p = farms.get(k).getCurrentProducts();
            		for (int i = 0; i < p.size(); i++) {
            			if (!networkProductList.contains(p.get(i).getName())) 
            			{
            				networkProductList.add(p.get(i).getName());
            				ProductMap.put(p.get(i).getName(), 1);
            			} else {
            				ProductMap.put(p.get(i).getName(), ProductMap.get(p.get(i).getName()) + 1);    // increment map
            			}
            		}
        			
        		}
        	}
        }
    	
    	List<String> mainFarmProduct = new ArrayList<String>();
    	for (int i = 0; i < this.getCurrentProducts().size(); i++)
    	{
    		String name = this.getCurrentProducts().get(i).getName();
    		mainFarmProduct.add(name);
    		
    		if(!networkProductList.contains(name) ) {
    			networkProductList.add(name);
    			ProductMap.put(name, 1);                                       // add product to map
    		} else {
    			ProductMap.put(name, ProductMap.get(name) + 1);                    // increment map
    		}
    	}
    	
    	// ACTUAL DISSIMILARITY CALULATION
    	for (int i = 0; i < networkProductList.size(); i++)
    	{
    		// if the product is produced by the main farmer ignore that product in the dissimilarity
    		if (mainFarmProduct.contains(networkProductList.get(i))) {
    			continue;
    			
    		} else {
    			// these products are not grown by the main farmer so it counts for the dissimilarity
    			dissimilarity = dissimilarity + (ProductMap.get(networkProductList.get(i)) / ((double)EdgeCount) );
    		}
    	}

        currentDissimilarity = dissimilarity/networkProductList.size();
        
		setUncertainty(currentDissimilarity);
	}
     /**
	 * Based on the current income level of the farmer calculate new satisfaction level.
	 * The farmer's income is set externally from farmdyn 
	 */
	private void updateSatisfaction() {		
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
	 * Based on the input parameter, calculate a tolerance level
	 */
	private void updateTolerance() {
		this.Tolerance = this.parameters.getB() * this.Tolerance;
	}
	/** 
	 * Each time period, t, call this function to increment the experience vector of this farm. 
	 * This experience vector is part of a shared experience matrix that all farms have
	 * If the farm is currently farming a product, then increase the experience of that product for that farm.
	 * For all other possible products in the experience vector for this farm, decrement the experience by one year.
	 * 
	 * Also increment the age of the farmer each time step.
	 */
	public void updateExperiencePlusAge() {
		List<String> productNames = new ArrayList<String>();				   // array of names of products for comparison
		
		int age = this.head.getAge();
		this.head.setAge(age + 1);                                             // increment farmers age each time period
		
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
	 * IncomeHistory = [1,2,3,4,5]
	 * where year 1 is the most recent income and year 5 is the oldest income
	 * @param income
	 */
	private void updateIncomeHistoryList (double income) {
		if(income == -1) return;											   // income is -1 for the first year due to initialization
		
		List<Double> temp = new ArrayList<Double>();                           // update array for new incomes
		temp.add(income);
		
		for (int i = 0; i< this.getMemory() - 1; i++) {
			temp.add(this.IncomeHistory.get(i));
		}
		
		setIncomeHistory(temp); 
	}
	/** 
	 * Set average income over the previous time periods.
	 * Exclude the first income period
	 */
	private void updateIncomeAverage() {
		List<Double> avgIncome = new ArrayList<Double>(this.IncomeHistory);
		avgIncome.remove(0);                                                   // remove first element
		double personalIncomeAverage = mean(avgIncome);
		setLastYearPersonalIncomeAverage(personalIncomeAverage);
	}
	/**
	 * update income uncertainty value
	 * @param farms
	 */
	private void updateIncomeUncertainty() {
		double personalIncomeChangePercent = (IncomeHistory.get(0) - lastYearPersonalIncomeAverage) /lastYearPersonalIncomeAverage;
		
		if ( (this.regionIncomeChangePercent - personalIncomeChangePercent) > 0 ) {
			this.IncomeUncertainty = 1;
		}
		else {
			this.IncomeUncertainty = 0;
		}
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
		
		double probability = this.getIncomeProbability();
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
	public int getStrategy() {
		return strategy;
	}
	public void setStrategy(int strategy) {
		this.strategy = strategy;
	}
	public double getIncomeProbability() {
		return incomeProbability;
	}
	public void setIncomeProbability(double incomeProbability) {
		this.incomeProbability = incomeProbability;
	}
	public double getIncomeUncertainty() {
		return IncomeUncertainty;
	}
	public void setIncomeUncertainty(double incomeUncertainty) {
		IncomeUncertainty = incomeUncertainty;
	}
	public double getRegionIncomeChangePercent() {
		return regionIncomeChangePercent;
	}
	public void setRegionIncomeChangePercent(double regionIncomeChangePercent) {
		this.regionIncomeChangePercent = regionIncomeChangePercent;
	}
	public double getLastYearPersonalIncomeAverage() {
		return lastYearPersonalIncomeAverage;
	}
	public void setLastYearPersonalIncomeAverage(double lastYearPersonalIncomeAverage) {
		this.lastYearPersonalIncomeAverage = lastYearPersonalIncomeAverage;
	}
}