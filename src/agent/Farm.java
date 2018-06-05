package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import reader.FarmDataMatrix;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import activity.Activity;
import decision.DecisionCalculator;
import java.lang.Math;

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
	private double Dissimilarity_ISB;										   // Dissimilarity Information Seeking Behavior (ISB)
	private double Income_ISB;										           // Income based Information Seeking Behavior (ISB)
	private double Dissimilarity_Tolerance;									   // Individual level of tolerance to differences in activities on the network
	private double Income_Tolerance;									       // Individual level of tolerance to differences in personal vs regional income changes
	private Graph<String, DefaultEdge> network; 							   // Social network of farmers
	private FarmDataMatrix experience;									       // experience (in years) of the each farm for each activity
	private FarmDataMatrix preferences;									       // preference (between 1 to 5) of each farm for each activity
	private List<Activity> currentActivities;								   // list of current activities each farm is engaged in
	private List<Activity> allActivities;									   // list of all possible activities
	private int strategy;													   // selected strategy (opt-out, optimize, imitate, repeat)
	private double incomeProbability;										   // for a region (list of farms) income is distributed normally. We can determine the probability of an income occuring in this distribution (CPD)
	private double regionIncomeChangePercent;								   // the percentage that the income of a region change (current_avg - historical_avg)/historical_avg
	private double lastYearPersonalIncomeAverage;							   // excluding most recent time period, average income of the specific farm
	private double learning_rate;											   // learning rate for specific farm. Calculated based on education level
	private List<Double> q_range;											   // q range (+,- values) for the learning rate vectors for the individual farm
	
	private double p_beta ;
	private double p_beta_s ;
	private double p_aspiration_coef ;
	private double p_activity_tolerance ;
	private double p_income_tolerance ;
	private double p_lambda ;
	private double p_alpha_plus ;
	private double p_alpha_minus ;
	private double p_phi_plus ;
	private double p_phi_minus;
	
	/**
	 * 
	 * @param name
	 * @param location
	 * @param socialNetwork
	 * @param incomeHistory
	 * @param personalIncomeAverage
	 * @param farmingExperience
	 * @param preferences
	 * @param activities
	 * @param activity_tolerance
	 * @param income_tolerance
	 * @param currentActivities
	 * @param farmHead
	 * @param beta
	 * @param beta_s
	 * @param aspiration_coef
	 * @param lambda
	 * @param alpha_plus
	 * @param alpha_minus
	 * @param phi_plus
	 * @param phi_minus
	 */
	public Farm(String name, Location location, Graph<String, DefaultEdge> socialNetwork, List<Double> incomeHistory, double personalIncomeAverage, 
			FarmDataMatrix farmingExperience, FarmDataMatrix preferences, List<Activity> activities, double activity_tolerance, double income_tolerance, 
			List<Activity> currentActivities, Person farmHead, double beta, double beta_s, double aspiration_coef, double lambda, double alpha_plus, 
			double alpha_minus, double phi_plus, double phi_minus) {
		
		this.setFarmName(name);
		this.setLocation(location);
		this.setNetwork(socialNetwork);
		this.setIncomeHistory(incomeHistory);
		this.setLastYearPersonalIncomeAverage(personalIncomeAverage);
		
		this.setExperience(farmingExperience);
		this.setPreferences(preferences);
		this.setActivities(activities);
		this.setDissimilarity_Tolerance(activity_tolerance);
		this.setIncome_Tolerance(income_tolerance);
		this.setCurrentActivites(currentActivities);
		this.setHead(farmHead);
		
		this.setP_beta(beta);
		this.setP_beta_s(beta_s);
		this.setP_aspiration_coef(aspiration_coef);
		this.setP_lambda(lambda);
		this.setP_alpha_plus(alpha_plus);
		this.setP_alpha_minus(alpha_minus);
		this.setP_phi_plus(phi_plus);
		this.setP_phi_minus(phi_minus);
		
		this.setP_activity_tolerance(activity_tolerance);
		this.setP_income_tolerance(income_tolerance);
		
	}
	/** 
	 * After the gams model has returned the simulation results. We update the individual farms with the new income values. 
	 * This allows the next stage of the ABM to run - we make a new activity set selection based on the ISB and the satisfaction levels. 
	 * 
	 * @param allFarms list of all the input farms
	 * @param income input value of farm
	 * @param probability of an income occurring in our distribution
	 */
	public void updateFarmData(List<Farm> allFarms, double income, double probability) {
		updateIncomeHistoryList(income);									       // for year = 1, we pass in -1 for income so we don't update the income
	    updateIncomeAverage();
	    setIncomeProbability(probability);

	    updateAspiration();
	    updateSatisfaction();									
	    updateIncome_ISB();
		updateDissimilarity_ISB(allFarms);
		updateISB_Tolerances();      
	}
	/** 
	 * When each agent makes a decision about which activity set to pursue, we take into account the neighboring farms, the income from last year, and the current activities. 
	 * We use this information to decide which of the four decisions the agent will pursue: exit, imitation, repetition, or optimization. 
	 * <br>
	 * Fuzzy logic is used for the optimization and imitation decisions to select a set of possible activities. These activities are then passed to a linear optimizer to get the best option. 
	 * 
	 * @param allFarms full list of all farms in system
	 * @return List containing the full fuzzy logic selection 
	 */
	public List<String> makeDecision(List<Farm> allFarms) {
	    List<String> fuzzyActionSet = new ArrayList<String>();				   // list of names of products from fuzzy logic
		DecisionCalculator cal = new DecisionCalculator(this, allFarms);       // calculator for the product selection
		
		if ((head.getAge() > 650)) {
			this.strategy = 1; //EXIT
		}
		else if ( (this.Dissimilarity_ISB >= this.Dissimilarity_Tolerance) || (this.Income_ISB >= this.Income_Tolerance) ) {  
			if (this.Satisfaction >= 0) {
				this.strategy = 2; //IMITATION
				fuzzyActionSet = cal.getImitationProducts();
			}
			else {
				this.strategy = 1; //EXIT
				System.out.println("exit: empty set");
			}
		}
		else {
			if (this.Satisfaction >= 0) {
				this.strategy = 4; //REPETITION
				for (int i = 0; i < this.getCurrentActivities().size(); i++) {
					fuzzyActionSet.add(this.getCurrentActivities().get(i).getName());
				} 
			}
			else {
				this.strategy = 3; //OPTIMIZATION
				fuzzyActionSet = cal.getOptimizeProducts();
			}
		}
		
		return fuzzyActionSet;
	}
	
	// update functions for farm parameters
	/**
	 * Using list of all current farms in the system and the social network of the main farm,
	 * update the main farm's Information Seeking Behavior (ISB) value based on the social network weight and the dissimilarity 
	 * between neighbor's activity types 
	 * 
	 * @param farms Input list of all farms in the system. 
	 */
	public void updateDissimilarity_ISB(List<Farm> farms) {
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
		setDissimilarity_ISB(currentDissimilarity);
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
		double alpha = this.getP_aspiration_coef();							   // alpha is the percentage of historical average
		
		aspiration = alpha*mean(IncomeHistory);
		
		setAspiration(aspiration);
	}
	/** 
	 * Based on the input parameter, calculate a tolerance level for dissimilarity and income. Percent of exogenously input tolerance levels. 
	 */
	private void updateISB_Tolerances() {
		this.Dissimilarity_Tolerance = this.getP_activity_tolerance(); // this was originally used as a modifier on the Tolerance levels, now we exogenously input the tolerance levels. 
		this.Income_Tolerance        = this.getP_income_tolerance();
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
		
		for (int i = 0; i< this.experience.getDataElementName().size(); i++ ) {
			int value = this.experience.getFarmDataElementValue(farmName, this.experience.getDataElementName().get(i));
			
			if (productNames.contains(this.experience.getDataElementName().get(i))) {
				value += 1;
			}
			else {
				value -= 1;
			}
			
			if(value > this.getMemory()) value = this.getMemory();
			if(value < 0) value = 0;
			
			this.experience.setFarmDataElementValue(farmName, this.experience.getDataElementName().get(i), value);
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
	 * update income Information Seeking Behavior (ISB) value based on personal income change and regional income change at a specific time period
	 */
	private void updateIncome_ISB() {
		double personalIncomeChangePercent = 0;								   // percent change in personal income
		personalIncomeChangePercent = (IncomeHistory.get(0) - lastYearPersonalIncomeAverage) /lastYearPersonalIncomeAverage;
		
		this.Income_ISB = this.regionIncomeChangePercent - personalIncomeChangePercent;
	}
	
	// helper functions
	/** 
	 * Calculate satisfaction score given income value
	 * @param income of farmer
	 * @return satisfaction
	 */
	private double calculateSatisfaction(double income) {
		double satisfaction = 0;
		
		double alpha_plus = this.getP_alpha_plus();
		double alpha_minus = this.getP_alpha_minus();
		double phi_plus = this.getP_phi_plus();
		double phi_minus = this.getP_phi_minus();
		
		double probability = this.getIncomeProbability();
		double lambda = this.getP_lambda();
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
	/** 
	 * Initialize a value for K based on the memory limit of each farm
	 * @return a double value that is K
	 */
	public double init_learning_rate() {
		double s = 1;
		double m1_ratio = 1/2.0;
		double m2_ratio = 1/8.0;

		double upper_q = 0.9;
		double lower_q = 0.65;
		double delta = 0.005;
		double k_upper = 1;
		double k_lower = 0.1;
		
		double ln_ratio = 0;
		
		double memory_limit = this.getMemory();

		while (k_upper > k_lower) {
			ln_ratio = -Math.log((1-upper_q)/upper_q);
			k_upper = ln_ratio/( Math.round(memory_limit * m1_ratio) * s);
			ln_ratio = -Math.log((1-lower_q)/lower_q);
			k_lower = ln_ratio/( Math.round(memory_limit * m2_ratio) * s);
			upper_q = upper_q - delta;
			lower_q = lower_q + delta;
		}

		double avg = (k_upper + k_lower) / 2.0;		
		return avg;
	}
	/** 
	 * Given a specific value for k, calculate all possible q (experience value) for all possible memory lengths. </br>
	 * So if memory is 5 years long, we calculate a q value for years 1 to 5. And using this set of q values we calcualte a standard deviation. </br>
	 * This standard deviation is used to set the upper and lower values for the q range. 
	 * @return
	 */
	public List<Double> calc_q_set() {
		double k = this.getLearningRate();
		List<Double> experience = new ArrayList<Double>();
		List<Double> q_range = new ArrayList<Double>();
		
		for (int i = 0; i < this.getMemory(); i++) {
			experience.add(  1 / (1 + Math.exp(-k * (i+1)) ));
		}
		
		// calculate standard deviation 
		double sd = 0;		
		for (int i=0; i<experience.size();i++)
		{
		    sd = sd + Math.pow(experience.get(i) - mean(experience), 2);
		}
		
		sd = Math.sqrt(sd/experience.size());
		
		q_range.add(sd);
		q_range.add(2*sd);
		
		return q_range;
	}

	// getters and setters for all fields
	public void setAspiration(double aspiration) {
		this.Aspiration = aspiration;
	}
	public void setDissimilarity_ISB(double dissimilarityISB) {
		this.Dissimilarity_ISB = dissimilarityISB;
	}
	public void setDissimilarity_Tolerance(double tolerance) {
		this.Dissimilarity_Tolerance = tolerance;
	}
	public void setSatisfaction(double satisfaction) {
		this.Satisfaction = satisfaction;
	}
	public int getAge() {
		return this.head.getAge();
	}
	public int getEducation() {
		return this.head.getEducation();
	}
	public FarmDataMatrix getPreferences() {
		return this.preferences;
	}
	public int getMemory() {
		return this.head.getMemory();
	}
	public Person getHead() {
		return this.head;
	}
	public void setHead(Person farmHead) {
		this.head = farmHead;
	}
	public String getFarmName() {
		return this.farmName;
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
		return this.network;
	}
	public void setNetwork(Graph<String, DefaultEdge> network) {
		this.network = network;
	}
	public double getDissimilarity_Tolerance() {
		return this.Dissimilarity_Tolerance;
	}
	public double getDissimilarity_ISB() {
		return this.Dissimilarity_ISB;
	}
	public double getAspiration() {
		return this.Aspiration;
	}
	public double getSatisfaction() {
		return this.Satisfaction;
	}
	public FarmDataMatrix getExperience() {
		return this.experience;
	}
	public void setExperience(FarmDataMatrix experience) {
		this.experience = experience;
	}
	public void setPreferences(FarmDataMatrix preferences) {
		this.preferences = preferences;
	}
	public void setCurrentActivites(List<Activity> activities) {
		this.currentActivities = activities;
	}
	public List<Activity> getCurrentActivities() {
		return this.currentActivities;
	}
	public List<Activity> getActivities() {
		return this.allActivities;
	}
	public void setActivities(List<Activity> activities) {
		this.allActivities = activities;
	}
	public List<Double> getIncomeHistory() {
		return this.IncomeHistory;
	}
	public void setIncomeHistory(List<Double> incomeHistory) {
		this.IncomeHistory = incomeHistory;
	}
	public int getStrategy() {
		return this.strategy;
	}
	public void setStrategy(int strategy) {
		this.strategy = strategy;
	}
	public double getIncomeProbability() {
		return this.incomeProbability;
	}
	public void setIncomeProbability(double incomeProbability) {
		this.incomeProbability = incomeProbability;
	}
	public double getIncome_ISB() {
		return this.Income_ISB;
	}
	public void setIncome_ISB(double incomeISB) {
		this.Income_ISB = incomeISB;
	}
	public double getRegionIncomeChangePercent() {
		return this.regionIncomeChangePercent;
	}
	public void setRegionIncomeChangePercent(double regionIncomeChangePercent) {
		this.regionIncomeChangePercent = regionIncomeChangePercent;
	}
	public double getLastYearPersonalIncomeAverage() {
		return this.lastYearPersonalIncomeAverage;
	}
	public void setLastYearPersonalIncomeAverage(double lastYearPersonalIncomeAverage) {
		this.lastYearPersonalIncomeAverage = lastYearPersonalIncomeAverage;
	}
	public double getIncome_Tolerance() {
		return this.Income_Tolerance;
	}
	public void setIncome_Tolerance(double income_Tolerance) {
		this.Income_Tolerance = income_Tolerance;
	}
	public List<Double> getQ_range() {
		if (mean(this.q_range) == 0) {
			this.setQ_range();
		}			
		return this.q_range;
	}
	public void setQ_range() {
		this.q_range = calc_q_set();
	}
	public double getLearningRate() {
		return this.learning_rate;
	}
	public void setLearningRate() {
		this.learning_rate = init_learning_rate();
	}
	public double getP_beta() {
		return this.p_beta;
	}
	public void setP_beta(double p_beta) {
		this.p_beta = p_beta;
	}
	public double getP_beta_s() {
		return this.p_beta_s;
	}
	public void setP_beta_s(double p_beta_s) {
		this.p_beta_s = p_beta_s;
	}
	public double getP_aspiration_coef() {
		return this.p_aspiration_coef;
	}
	public void setP_aspiration_coef(double p_aspiration_coef) {
		this.p_aspiration_coef = p_aspiration_coef;
	}
	public double getP_activity_tolerance() {
		return p_activity_tolerance;
	}
	public void setP_activity_tolerance(double p_activity_tolerance) {
		this.p_activity_tolerance = p_activity_tolerance;
	}
	public double getP_income_tolerance() {
		return this.p_income_tolerance;
	}
	public void setP_income_tolerance(double p_income_tolerance) {
		this.p_income_tolerance = p_income_tolerance;
	}
	public double getP_lambda() {
		return p_lambda;
	}
	public void setP_lambda(double p_lambda) {
		this.p_lambda = p_lambda;
	}
	public double getP_alpha_plus() {
		return p_alpha_plus;
	}
	public void setP_alpha_plus(double p_alpha_plus) {
		this.p_alpha_plus = p_alpha_plus;
	}
	public double getP_alpha_minus() {
		return p_alpha_minus;
	}
	public void setP_alpha_minus(double p_alpha_minus) {
		this.p_alpha_minus = p_alpha_minus;
	}
	public double getP_phi_plus() {
		return p_phi_plus;
	}
	public void setP_phi_plus(double p_phi_plus) {
		this.p_phi_plus = p_phi_plus;
	}
	public double getP_phi_minus() {
		return p_phi_minus;
	}
	public void setP_phi_minus(double p_phi_minus) {
		this.p_phi_minus = p_phi_minus;
	}
}












