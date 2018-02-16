package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import reader.FarmProductMatrix;
import reader.Parameters;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import activity.Activity;
import decision.DecisionCalculator;

/** 
 * Farm object contains people and parameters associated with each farm, as well as a copy of activity list, preferences and experiences, and network connections
 * 
 * @author kellerke
 *
 */
public class Farm {
	private String farmName;												   // name of farm
	private Person head;													   // person object indicating head of farm
	private Location location;												   // geographic location of farm
	private double Satisfaction;											   // Satisfaction level of farm
	private List<Double> IncomeHistory;										   // list of previous income values
	private double Aspiration;												   // Aspiration level of the farm
	private double Uncertainty;												   // Information Seeking level
	private double IncomeUncertainty;										   // Information seeking level based on income change
	private double Tolerance;												   // Individual level of tolerance to uncertain future
	private Graph<String, DefaultEdge> network; 							   // Social network of farmers
	private FarmProductMatrix experience;									   // experience (in years) of the each farm for each activity
	private FarmProductMatrix preferences;									   // preference (between 1 to 5) of each farm for each activity
	private List<Activity> currentActivities;								   // list of current activities each farm is engaged in
	private List<Activity> allActivities;									   // list of all possible activities
	private Parameters parameters;											   // simulation parameters
	private int strategy;													   // selected strategy (opt-out, optimize, imitate, repeat)
	private double incomeProbability;										   // for a region (list of farms) income is distributed normally. We can determine the probability of an income occuring in this distribution (CPD)
	private double regionIncomeChangePercent;								   // the percentage that the income of a region change (current_avg - historical_avg)/historical_avg
	private double lastYearPersonalIncomeAverage;							   // excluding most recent time period, average income of the specific farm
	
	/** 
	 * Constructor method for farm object
	 * @param name	of the farm
	 * @param location	of the farm
	 * @param socialNetwork		social network of the farm
	 * @param incomeHistory	history to initialize farm
	 * @param personalIncomeAverage	average of income except most recent time period
	 * @param farmingExperience	shared experience matrix
	 * @param preferences			activity preference
	 * @param activities	full activity list
	 * @param tolerance	tolerance to uncertainty
	 * @param currentActivities	list of current farm activities during initalization
	 * @param farmHead	person object to lead farm
	 * @param parameters	simulation parameters
	 */
	public Farm(String name, Location location, Graph<String, DefaultEdge> socialNetwork, List<Double> incomeHistory,
			double personalIncomeAverage, FarmProductMatrix farmingExperience, FarmProductMatrix preferences,
			List<Activity> activities, double tolerance, List<Activity> currentActivities, Person farmHead,
			Parameters parameters) {

		this.setFarmName(name);
		this.setLocation(location);
		this.setNetwork(socialNetwork);
		this.setIncomeHistory(incomeHistory);
		this.setLastYearPersonalIncomeAverage(personalIncomeAverage);
		this.setExperience(farmingExperience);
		this.setPreferences(preferences);
		this.setActivities(activities);
		this.setTolerance(tolerance);
		this.setCurrentActivites(currentActivities);
		this.setHead(farmHead);
		this.setParameters(parameters);
	}
	/**
	 * Alternative constructor
	 */
	public Farm() {
		// TODO Auto-generated constructor stub
	}
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
	 * create product selection calculator for this farm based on consumat model, decide which of the five decisions the farm will pursue
	 * 
	 * Fuzzy logic is used for optimization and imitation decisions to select a set of products to pursue. 
	 * 
	 * For the sensitivity testing phase, we need to 'fake' a linear programming model so we select half of the set to return as our final decision. 
	 * We keep track of both sets for use during the sensitivity analysis
	 * 
	 * @param allFarms full list of all farms in system
	 * @return List containing the full fuzzy logic selection 
	 */
	public List<String> makeDecision(List<Farm> allFarms) {
	    List<String> fuzzyActionSet = new ArrayList<String>();				   // list of names of products from fuzzy logic
		//List<Activity> current = new ArrayList<Activity>();					   // current activities (objects - not names) in system 
		DecisionCalculator cal = new DecisionCalculator(this, allFarms);       // calculator for the product selection

		if ((head.getAge() > 650)) {
			//System.out.println("EXIT");
			this.strategy = 1;
		}
		else if (this.Uncertainty >= this.Tolerance) {
			if (this.Satisfaction >= 0) {
				//System.out.println("IMITATION");
				this.strategy = 2;
				fuzzyActionSet = cal.getImitationProducts();
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
				for (int i = 0; i < this.getCurrentActivities().size(); i++) {
					fuzzyActionSet.add(this.getCurrentActivities().get(i).getName());
				} 
			}
			else {
				//System.out.println("OPTIMIZATION");
				this.strategy = 3;
				fuzzyActionSet = cal.getOptimizeProducts();
			}
		}

		/*
		// this is to build a list of product objects - not just names
		for (int k = 0; k < minProductSet.size(); k++) {
			for(int i = 0; i<allActivities.size(); i++) {
				if (allActivities.get(i).getName().equals(minProductSet.get(k) )) {
					int ID = allActivities.get(i).getID();
					Activity p = new Activity(ID, minProductSet.get(k)); 
					current.add(p);
				}
			}
		}		
		
		this.setCurrentActivites(current);                                      // update current products for the farm instance
		*/
		return fuzzyActionSet;
	}
	
	// update functions for farm parameters
	/**
	 * Using list of all current farms in the system and the social network of the main farm,
	 * update the main farm's uncertainty value based on the social network weight and the dissimilarity 
	 * between neighbor's activity types 
	 * 
	 * @param farms Input list of all farms in the system. 
	 */
	public void updateUncertainty(List<Farm> farms) {
        double currentDissimilarity = 0;									   // similarity value of a farm 
        int EdgeCount = 0;													   // how many edges does this farm have (ie neighbors)
		int totalFarms = 0;													   // how many total farms are there in the network
        List<String>  networkActivityList = new ArrayList<String>();		   // intermediate variable of just names, not product objects
        Map<String, Integer> activityMap = new HashMap<String,Integer>();      // map of all activities on network, with count of often it's produced
        Double dissimilarity = 0.0;											   // dissimilarity value for farm
        Set<DefaultEdge> E;                                                    // set of edges in network with this farm at the head
        double w = 0;														   // weight of each network connection
        Iterator<DefaultEdge> I;											   // iterator through all edges
        List<String> mainFarmActivity = new ArrayList<String>();			   // the main farm's list of activities for comparison to network activities
    		
		E = this.network.outgoingEdgesOf(this.farmName);
        totalFarms = farms.size();
        I = E.iterator();
        
        for (int k = 0; k < totalFarms; k++) {
        	if (!farms.get(k).getFarmName().equals(this.getFarmName()) ) {
        		w = this.getNetwork().getEdgeWeight(I.next());						   // weight of social tie between main farm and farm i
        		if (w > 0) {
        			EdgeCount++;
            		List<Activity> p = farms.get(k).getCurrentActivities();
            		for (int i = 0; i < p.size(); i++) {
            			if (!networkActivityList.contains(p.get(i).getName())) 
            			{
            				networkActivityList.add(p.get(i).getName());
            				activityMap.put(p.get(i).getName(), 1);
            			} else {
            				activityMap.put(p.get(i).getName(), activityMap.get(p.get(i).getName()) + 1);    // increment map that tracks how often a product occurs in the network (ie Maize occurs 5 times in network -> Maize, 5)
            			}
            		}
        			
        		}
        	}
        }
    	
    	for (int i = 0; i < this.getCurrentActivities().size(); i++)
    	{
    		String name = this.getCurrentActivities().get(i).getName();
    		mainFarmActivity.add(name);
    		
    		if(!networkActivityList.contains(name) ) {
    			networkActivityList.add(name);
    			activityMap.put(name, 1);                                       // add product to map of entire network (
    		} else {
    			activityMap.put(name, activityMap.get(name) + 1);              // increment map
    		}
    	}
    	
    	// dissimilarity calculation based on difference in neighbor vs main farm product sets
    	for (int i = 0; i < networkActivityList.size(); i++)
    	{
    		// if the activity is done by the main farmer ignore that product in the dissimilarity
    		if (mainFarmActivity.contains(networkActivityList.get(i))) {
    			continue;
    			
    		} else {
    			// these products are not grown by the main farmer so it counts for the dissimilarity
    			dissimilarity = dissimilarity + (activityMap.get(networkActivityList.get(i)) / ((double)EdgeCount) );
    		}
    	}

        currentDissimilarity = dissimilarity/networkActivityList.size();
		setUncertainty(currentDissimilarity);
	}
     /**
	 * Based on the current income level of the farmer calculate new satisfaction level.
	 * The farmer's income is set externally from LP simulation tool, or randomly generated from distribution
	 */
	private void updateSatisfaction() {		
		double current_satisfaction = currentSatisfaction();			       // current satisfaction level
		setSatisfaction(current_satisfaction);                                 // uses updated income history
	}
	/** 
	 * Based on the historical income data, calculate the aspiration level as a percentage of historical income.
	 */
	private void updateAspiration() {
		double aspiration = 0;												   // calculated aspiration level
		double alpha = this.parameters.getA();								   // alpha is the percentage of historical average
		
		aspiration = alpha*mean(IncomeHistory);
		
		setAspiration(aspiration);
	}
	/** 
	 * Based on the input parameter, calculate a tolerance level. Percent of exogenously derived tolerance level. 
	 */
	private void updateTolerance() {
		this.Tolerance = this.parameters.getB() * this.Tolerance;
	}
	/** 
	 * Each time period, t, call this function to increment the experience vector of this farm. 
	 * This experience vector is part of a shared experience matrix that all farms contain. 
	 * If the farm is currently farming a product, then increase the experience of that product for that farm.
	 * For all other possible products in the experience vector for this farm, decrement the experience by one year.
	 * <br>
	 * Increment the age of the farmer each time step.
	 */
	public void updateExperiencePlusAge() {
		List<String> productNames = new ArrayList<String>();				   // array of names of products for comparison
		int age = this.head.getAge();										   // age of the farmer
		this.head.setAge(age + 1);                                             // increment farmers age each time period
		
		for (int i = 0; i<this.getCurrentActivities().size(); i++) {
			productNames.add(this.getCurrentActivities().get(i).getName());
		}
		
		for (int i = 0; i< this.experience.getProductName().size(); i++ ) {
			int value = this.experience.getFarmProductValue(farmName, this.experience.getProductName().get(i));
			
			if (productNames.contains(this.experience.getProductName().get(i))) {
				value += 1;
			}
			else {
				value -= 1;
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
		List<Double> temp = new ArrayList<Double>();                           // update array for new incomes
		if(income == -1) return;											   // income is -1 for the first year due to initialization
		
		temp.add(income);													   // start income list with updated income
		for (int i = 0; i< this.getMemory() - 1; i++) {
			temp.add(this.IncomeHistory.get(i));							   // add all but oldest income to income list
		}
		
		setIncomeHistory(temp); 
	}
	/** 
	 * Set average income over the previous time periods.
	 * Exclude the first income period
	 */
	private void updateIncomeAverage() {
		List<Double> avgIncome = new ArrayList<Double>(this.IncomeHistory);    // copy of income history
		avgIncome.remove(0);                                                   // remove most recent year from historical avg. Most recent year is used to calculate percent change compared to avg. 
		double personalIncomeAverage = mean(avgIncome);						   // average of historical income
		setLastYearPersonalIncomeAverage(personalIncomeAverage);
	}
	/**
	 * update income uncertainty value based on personal income change and regional income change at a specific time period
	 */
	private void updateIncomeUncertainty() {
		double personalIncomeChangePercent = 0;								   // percent change in personal income
		
		personalIncomeChangePercent = (IncomeHistory.get(0) - lastYearPersonalIncomeAverage) /lastYearPersonalIncomeAverage;
		
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
		List<Double> sat = new ArrayList<Double>();						       // calculate satisfaction for each individual income in income history
		
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
	public void setCurrentActivites(List<Activity> activities) {
		this.currentActivities = activities;
	}
	public List<Activity> getCurrentActivities() {
		return this.currentActivities;
	}
	public List<Activity> getActivities() {
		return allActivities;
	}
	public void setActivities(List<Activity> activities) {
		this.allActivities = activities;
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