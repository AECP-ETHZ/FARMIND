package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import reader.FarmDataMatrix;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import activity.Activity;
import fuzzy_logic.FuzzyLogicCalculator;
import java.lang.Math;

/** 
 * Farm object contains farm characteristics, an activity list, activity preferences, experience of performing activities and a social network. 
 *
 * @author kellerke
 */
public class Farm {
	private String farmName;												   // name of farm
	private Person head;													   // person object indicating head of farm
	private Location location;												   // geographic location of farm
	private double Satisfaction;											   // Satisfaction level of farm
	private List<Double> IncomeHistory;										   // list of previous income values
	private double Activity_Dissimilarity;									   // Dissimilarity of this farm with respect to network for each Activity
	private double Income_Dissimilarity;									   // Dissimilarity of this farm with respect to network for each Income
	
	private Graph<String, DefaultEdge> network; 							   // Social network of farmers
	private FarmDataMatrix experience;									       // experience (in years) of the each farm for each activity
	private FarmDataMatrix preferences;									       // preference (between 1 to 5) of each farm for each activity
	private List<Activity> currentActivity;								       // list of current activities each farm is engaged in
	private List<Activity> allActivities;									   // list of all possible activities
	private int strategy;													   // selected strategy (opt-out, optimize, imitate, repeat)
	private double averagePopulationIncomeChangeRate;						   // the average rate of change for all incomes in the population
	private double averagePersonalIncomeChangeRate;						       // the average rate of change for incomes between all years in the memory length
	private double learning_rate;											   // learning rate for specific farm. Calculated based on education level
	private List<Double> q_range;											   // q range (+,- values) for the learning rate vectors for the individual farm
	
	private double p_beta_l ;												   // Parameter for the Beta Learning
	private double p_beta_s ;											       // Parameter for the Beta Social
	private double p_beta_p;												   // Parameter for the Beta Preference
	private double p_reference_income;										   // Parameter for reference for historical income 
	private double p_aspiration_coef ;										   // Parameter for the Satisfaction Calculation
	private double p_activity_tolerance_coef ;								   // Parameter for the individual level of tolerance to differences in activities on the network
	private double p_income_tolerance_coef ;								   // Parameter for the individual level of tolerance to differences in personal vs population income changes
	private double p_lambda ;												   // Parameter for the Lambda used for the Satisfaction Calculation
	private double p_alpha_plus ;											   // Parameter for the Alpha Plus used for the Satisfaction Calculation
	private double p_alpha_minus ;											   // Parameter for the Alpha Minus used for the Satisfaction Calculation
	private double p_phi_plus ;												   // Parameter for the Phi Plus used for the Satisfaction Calculation
	private double p_phi_minus;												   // Parameter for the Phi Minus used for the Satisfaction Calculation
	private double p_opt_fuzzy_size;										   // Parameter to control the optimization fuzzy logic size
	private double p_imt_fuzzy_size;										   // Parameter to control the imitation fuzzy logic size
	private int p_ranking_version;											   // Parameter to control what fuzzy ranking algorithm is selected
	private static final Logger LOGGER = Logger.getLogger("FARMIND_LOGGING");  // logger for system 
	
	/**
	 * This constructor sets up the parameters associated with a farm. 
	 * 
	 * @param name :: name of the farm agent
	 * @param location :: location of the farm agent
	 * @param socialNetwork :: social network of the agent shared among the population
	 * @param incomeHistory :: income history for each agent
	 * @param farmingExperience :: matrix of farming experience for each activity
	 * @param preferences :: agent preference matrix for activities
	 * @param activities :: list of all activities
	 * @param activity_tolerance :: tolerance for activity differences
	 * @param income_tolerance :: tolerance for income differences
	 * @param currentActivity :: current activity for agent
	 * @param farmHead :: person object to represent main farmer
	 * @param beta_l :: parameter for fuzzy logic
	 * @param beta_s :: parameter for fuzzy logic
	 * @param beta_p :: parameter for fuzzy logic
	 * @param reference_income :: parameter for agent to select high or low income levels
	 * @param aspiration_coef :: aspiration value
	 * @param lambda :: value for lambda for satisfaction
	 * @param alpha_plus :: for satisfaction calculation
	 * @param alpha_minus :: for satisfaction calculation
	 * @param phi_plus :: for satisfaction calculation
	 * @param phi_minus :: for satisfaction calculation
	 * @param opt_fuzzy_size :: how large of a fuzzy set to return
	 * @param learning_rate :: if learning rate is greater than 0, then we use that value, otherwise calculate internally
	 * @param ranking_version :: input ranking version, 0 is default
	 * @param imt_fuzzy_size :: fuzzy size for imitation version
	 */
	public Farm(String name, Location location, Graph<String, DefaultEdge> socialNetwork, List<Double> incomeHistory,  
			FarmDataMatrix farmingExperience, FarmDataMatrix preferences, List<Activity> activities, double activity_tolerance, double income_tolerance, 
			List<Activity> currentActivity, Person farmHead, double beta_l, double beta_s, double beta_p, double reference_income, double aspiration_coef, double lambda, double alpha_plus, 
			double alpha_minus, double phi_plus, double phi_minus, double opt_fuzzy_size, double imt_fuzzy_size, int ranking_version, double learning_rate) {
		
		this.setFarmName(name);
		this.setLocation(location);
		this.setNetwork(socialNetwork);
		this.setIncomeHistory(incomeHistory);
		this.setExperience(farmingExperience);
		this.setPreferences(preferences);
		this.setActivities(activities);
		this.setCurrentActivity(currentActivity);
		this.setHead(farmHead);
		this.setP_beta_l(beta_l);
		this.setP_beta_s(beta_s);
		this.setP_beta_p(beta_p);
		this.setP_reference_income(reference_income);
		this.setP_aspiration_coef(aspiration_coef);
		this.setP_lambda(lambda);
		this.setP_alpha_plus(alpha_plus);
		this.setP_alpha_minus(alpha_minus);
		this.setP_phi_plus(phi_plus);
		this.setP_phi_minus(phi_minus);
		this.setP_activity_tolerance_coef(activity_tolerance);
		this.setP_income_tolerance_coef(income_tolerance);
		this.setP_opt_fuzzy_size(opt_fuzzy_size);
		this.setP_imt_fuzzy_size(imt_fuzzy_size);
		this.setP_ranking_version(ranking_version);
		this.setLearningRate(learning_rate);
	}
	
	/** 
	 * This function executes strategic decision-making for all farms. 
	 * Comparing satisfaction to aspiration and dissimilarities to tolerance, agents decide which of the four strategies to pursue: repetition, optimization, imitation or opt-out.
	 * 
	 * @param allFarms :: full list of all farms in system
	 * @param cmd :: command line object that contains parameters for model
	 * @return ActivitySet :: list of activity options for a farm to select 
	 */
	public List<String> decideActivitySet(List<Farm> allFarms, Properties cmd) {
	    List<String> ActivitySet = new ArrayList<String>();				                           // list of activities from fuzzy logic
		FuzzyLogicCalculator fuzzyLogicCalc = new FuzzyLogicCalculator(this, allFarms);            // calculator for the activity selection
		
		// if the farming is currently performing opt-out, then continue performing opt-out
		if (this.strategy == 1) {
			this.strategy = 1;
			return ActivitySet;
		}
		
		if ((head.getAge() > 650)) {
			this.strategy = 1;     //OPT-OUT (The farmer retires.)
			return ActivitySet;
		}
		
		// modified simulation using only satisfaction
		if ( Integer.parseInt(cmd.getProperty("uncertainty")) == 0) {
			if (this.Satisfaction >= this.getP_aspiration_coef() ) {
				this.strategy = 4; //REPETITION
				for (int i = 0; i < this.getCurrentActivity().size(); i++) {
					ActivitySet.add(this.getCurrentActivity().get(i).getName());
				} 
			}
			else {
				if (this.Satisfaction < this.getP_aspiration_coef()) {
					this.strategy = 3; //OPTIMIZATION
					if (cmd.getProperty("modelName").equals("WEEDCONTROL")) {
						for (int i = 0; i < this.getActivities().size(); i++) {                    // for optimization of weedcontrol, return all activities
							ActivitySet.add(this.getActivities().get(i).getName());
						} 
					} else {
						ActivitySet = fuzzyLogicCalc.getOptimizationActivities();
					}
				} 
			}
		}
		
		// Full simulation using dissimilarity and satisfaction
		else {
			if ( (this.Activity_Dissimilarity >= this.p_activity_tolerance_coef) || (this.Income_Dissimilarity >= this.p_income_tolerance_coef) ) {  
				if (this.Satisfaction >= this.getP_aspiration_coef()) {
					this.strategy = 2; //IMITATION
					ActivitySet = fuzzyLogicCalc.getImitationActivities();
				}
				else {
					this.strategy = 1; //OPT-OUT
					LOGGER.fine(String.format("Opt-out made: %d for farm %s", this.strategy, this.farmName));
				}
			}
			else {
				if (this.Satisfaction >= this.getP_aspiration_coef()) {
					this.strategy = 4; //REPETITION
					for (int i = 0; i < this.getCurrentActivity().size(); i++) {
						ActivitySet.add(this.getCurrentActivity().get(i).getName());
					} 
				}
				else {
					this.strategy = 3; //OPTIMIZATION
					ActivitySet = fuzzyLogicCalc.getOptimizationActivities();
//					if (cmd.getProperty("modelName").equals("WEEDCONTROL")) {
//						ActivitySet = fuzzyLogicCalc.getOptimizationActivities();
//						for (int i = 0; i < this.getActivities().size(); i++) {                    // for optimization of weedcontrol, return all activities
//							ActivitySet.add(this.getActivities().get(i).getName());
//						} 
//					} else {
//						ActivitySet = fuzzyLogicCalc.getOptimizationActivities();
//					}
				} 
			}
		}

		return ActivitySet;
	}
	
	// Update functions for farm parameters
	/** 
	 * This function updates all updatedable parameters on the farm based on new income and activity
	 * 
	 * @param allFarms :: list of all farms
	 * @param income :: income value of farm
	 * @param activity :: activity list to update farm after MP time step iteration
	 */
	public void updateFarmParameters(List<Farm> allFarms, double income, List<Activity> activity) {
		updateIncomeHistoryList(income);									   // for year = 1, we pass in -1 for income so we don't update the income 
	    updateAveragePersonalIncomeChangeRate();
	    
	    if (income != -1 && activity != null) {								   // if no activity, then skip update
	    	setCurrentActivity(activity);
	    }    
	    updateSatisfaction();									
	    updateIncomeDissimilarity();										   // in the simulation loop in Main, we update the populationIncomeChangePercent 
		updateActivityDissimilarity(allFarms);
	}
	/**
	 * This function updates farms with values of dissimilarity in terms of activity. 
	 * The dissimilarity is calculated as the degree that the activity one performs is different from those its peers in social networks perform.  
	 * 
	 * @param farms :: list of farms 
	 */
	public void updateActivityDissimilarity(List<Farm> farms) {
        double currentDissimilarity = 0;									   // similarity value of a farm 
        int edgeCount = 0;													   // how many edges does this farm have (ie neighbors)
		int totalFarms = 0;													   // how many total farms are there in the network
		
		// Network Activity includes all farms on the network with a weight greater than 0 AND includes this (main) farm. 
        List<String>  networkActivityList = new ArrayList<String>();		   // Intermediate variable of just names, not activity objects (allows easier search)
        
        Map<String, Integer> activityMap = new HashMap<String,Integer>();      // map of all activities on network, with count of often it's produced 
        Double dissimilarity = 0.0;											   // dissimilarity value for farm
        Set<DefaultEdge> edge;                                                 // set of edges in network with this farm at the head
        double w = 0;														   // weight of each network connection
        Iterator<DefaultEdge> I;											   // iterator through all edges
        List<String> thisFarmActivityList = new ArrayList<String>();		   // the main farm's list of activities for comparison to network activities
    		
		edge = this.network.outgoingEdgesOf(this.farmName);
        totalFarms = farms.size();
        I = edge.iterator();
        
        for (int k = 0; k < totalFarms; k++) {
        	if (!farms.get(k).getFarmName().equals(this.getFarmName()) ) {
        		w = this.getNetwork().getEdgeWeight(I.next());						   // weight of social tie between main farm and farm i
        		if (w > 0) {
        			edgeCount++;
            		List<Activity> p = farms.get(k).getCurrentActivity();
            		for (int i = 0; i < p.size(); i++) {
            			if (!networkActivityList.contains(p.get(i).getName())) 
            			{
            				networkActivityList.add(p.get(i).getName());
            				activityMap.put(p.get(i).getName(), 1);
            			} else {
            				activityMap.put(p.get(i).getName(), activityMap.get(p.get(i).getName()) + 1);    // increment map that tracks how often an activity occurs in the network (ie Maize occurs 5 times in network -> Maize, 5)
            			}
            		}
        			
        		}
        	}
        }
    	
    	for (int i = 0; i < this.getCurrentActivity().size(); i++)
    	{
    		String name = this.getCurrentActivity().get(i).getName();
    		thisFarmActivityList.add(name);
    		
    		if(!networkActivityList.contains(name) ) {
    			networkActivityList.add(name);
    			activityMap.put(name, 1);                                       // add activity to map of entire network (
    		} else {
    			activityMap.put(name, activityMap.get(name) + 1);              // increment map
    		}
    	}
    	
    	// Dissimilarity calculation based on difference between activity sets of this farm and its peers 
    	for (int i = 0; i < networkActivityList.size(); i++)
    	{
    		// Ignore the activity in the dissimilarity if it is performed by this farm
    		if (thisFarmActivityList.contains(networkActivityList.get(i))) {
    			continue;
    			
    		} else {
    	    // Count activities not performed by this farm for the dissimilarity
    			dissimilarity = dissimilarity + (activityMap.get(networkActivityList.get(i)) / ((double)edgeCount) );
    		}
    	}

        currentDissimilarity = dissimilarity / networkActivityList.size();
		setActivity_Dissimilarity(currentDissimilarity);
	}
	
	/**
	 * This function updates farms with values of dissimilarity in terms of income growth. 
	 * The dissimilarity is calculated as the degree that the growth rate of one's income is lower the average level of the population.
	 */
	public void updateIncomeDissimilarity() {
		
		this.Income_Dissimilarity = averagePopulationIncomeChangeRate - averagePersonalIncomeChangeRate;
	}
	
     /**
	 * Based on the current income level of the farmer calculate new satisfaction level.
	 * The farmer's income is set externally from LP simulation tool, or randomly generated from distribution
	 */
	public void updateSatisfaction() {		
		double current_satisfaction = currentSatisfaction();			       // current satisfaction level
		setSatisfaction(current_satisfaction);                                 // uses updated income history
	}

	/** 
	 * Each time period, t, call this function to increment the experience vector of this farm. 
	 * This experience vector is part of a shared experience matrix that all farms contain. 
	 * If the farm is currently performing an activity, then increase the experience of that activity for that farm.
	 * For all other possible activities in the experience vector for this farm, decrement the experience by one year.
	 * <br>
	 * Increment the age of the farmer each time step.
	 */
	public void updateExperience() {
		List<String> activityNames = new ArrayList<String>();				   // array of names of activities for comparison

		for (int i = 0; i<this.getCurrentActivity().size(); i++) {
			activityNames.add(this.getCurrentActivity().get(i).getName());
		}
		
		for (int i = 0; i< this.experience.getDataElementName().size(); i++ ) {
			double value = this.experience.getFarmDataElementValue(farmName, this.experience.getDataElementName().get(i)); 
			
			if (activityNames.contains(this.experience.getDataElementName().get(i))) {
				value = value*0.8 + 1;
			}
			else {
				value = value*0.8;      // Experience for all activities decays by 20% per year. Do the decay first, then increase the value for new activities.
			}
			
			if(value > this.getMemory()) value = this.getMemory();
			if(value < 0) value = 0;
			
			this.experience.setFarmDataElementValue(farmName, this.experience.getDataElementName().get(i), value);
		}	
	}
	
	/** 
	 * Each time period, t, call this function to increment the experience vector of this farm. 
	 * This experience vector is part of a shared experience matrix that all farms contain. 
	 * If the farm is currently performing an activity, then increase the experience of that activity for that farm.
	 * For all other possible activities in the experience vector for this farm, decrement the experience by one year.
	 * <br>
	 * Increment the age of the farmer each time step.
	 */
	public void updateAge() {
		int age = this.head.getAge();										   // age of the farmer
		this.head.setAge(age + 1);                                             // increment farmers age each time period
	}
		
	/**
	 * Update income history by removing oldest income and replacing with new income
	 * Income is an array in the format of [year1_income, year2_income, year3_income, ... yearM_income]
	 * where year1 is the most recent income and yearM is the oldest income
	 * @param income
	 */
	private void updateIncomeHistoryList (double income) {
		List<Double> income_hist = new ArrayList<Double>();                    // Add new income to the list of income history
		if(income == -1) return;											   // income is -1 for the first year due to initialization
		
		income_hist.add(income);											   // start income list with updated income for year 1

		for (int i = 0; i< this.getMemory()-1; i++) {
			income_hist.add(this.IncomeHistory.get(i));						   // add all but oldest income (year N) to income list 
		}
		
		setIncomeHistory(income_hist); 
	}
	/** 
	 * Update personal income change rate over the previous time periods and include the current year. 
	 * Income is an array of [year1_income, year2_income, year3_income, ... yearN_income]
	 * change rate is calculated for all years as (Year_n-1 - Year_n) / year_n
	 * We then set the mean of all the rates of change including the most recent year. 
	 */
	public void updateAveragePersonalIncomeChangeRate() {	
		List<Double> differenceIncomeYears = new ArrayList<Double>();
		double historicalIncomeChangeRate = 0;								   
		
		int meanYearCount = 2;                                                 // number of years to count in the past for the mean calculation
		
		for(int i = meanYearCount; i > 0; i-- ) {
			double diff = (this.IncomeHistory.get(i-1) -  this.IncomeHistory.get(i)) /  this.IncomeHistory.get(i);
			differenceIncomeYears.add( diff );   
		}
		
		historicalIncomeChangeRate = mean(differenceIncomeYears);
		
		setAveragePersonalIncomeChangeRate(historicalIncomeChangeRate);
	}
	
	// Helper functions
	/** 
	 * Calculate satisfaction score given income value
	 * @param income :: income of farm
	 * @param probability :: probability that the income occurs
	 * @return satisfaction :: satisfaction derived from income
	 */
	private double calculateSatisfaction(double income, double probability, double prob_delta_pos, double prob_delta_neg) {
		double satisfaction = 0;	
		double alpha_plus = this.getP_alpha_plus();
		double alpha_minus = this.getP_alpha_minus();
		double phi_plus = this.getP_phi_plus();
		double phi_minus = this.getP_phi_minus();
		double lambda = this.getP_lambda();
		double value = 0;                                                      // value function
		double probWeighting = 0;                                              // probability weighting function
		double probWeighting_delta = 0;
		
		double phi = 0;

		if (income >= this.p_reference_income) {
			value = Math.pow(income, alpha_plus);
			probability = 1-probability;
			probWeighting = ( Math.pow(probability, phi_plus) ) / Math.pow( (Math.pow(probability, phi_plus) + Math.pow((1 - probability), phi_plus)), (1/phi_plus) );
			
			probability = 1 - prob_delta_pos;
			probWeighting_delta = ( Math.pow(probability, phi_plus) ) / Math.pow( (Math.pow(probability, phi_plus) + Math.pow((1 - probability), phi_plus)), (1/phi_plus) );
			
			phi = probWeighting - probWeighting_delta;
		} 
		else {
			value = (-1)*lambda*Math.pow(income, alpha_minus);
			
			probWeighting = ( Math.pow(probability, phi_minus) ) / Math.pow( (Math.pow(probability, phi_minus) + Math.pow((1 - probability), phi_minus)), (1/phi_minus) );
			
			probability = prob_delta_neg;
			probWeighting_delta = ( Math.pow(probability, phi_minus) ) / Math.pow( (Math.pow(probability, phi_minus) + Math.pow((1 - probability), phi_minus)), (1/phi_minus) );
			
			phi = probWeighting - probWeighting_delta;
		}

		satisfaction = value*phi;

		return satisfaction;
	}
	/**
	 * From the farm income history, calculate current satisfaction level as the average of historical satisfaction
	 * Build a normal distribution based on historical income and for each income at time period T, sample the probability and use that in the satisfaction
	 * @return mean :: mean of all satisfaction values
	 */
	private double currentSatisfaction() {
		List<Double> current_satisfaction = new ArrayList<Double>();						       // calculate satisfaction for each income value in the list of income history
		double probability = 0;
		double prob_delta_pos = 0; 
		double prob_delta_neg = 0; 
		double mean = mean(this.IncomeHistory);
		double std = std(this.IncomeHistory);
		
		if (mean == 0 && std == 0) {
			mean = 1;
			std = 1;
			LOGGER.finer( String.format("Mean/STD set to 1 for farm %S as it is in opt-out", this.getFarmName()  ) );
		}
		if (mean != 0 && std == 0) {
			System.out.println(this.getStrategy());
			System.out.println(mean);
			LOGGER.severe( String.format("The standard deviation of historical incomes is 0 for farm %S ", this.getFarmName()  ) );
			System.exit(0);     
		}
		
		NormalDistribution normal = new NormalDistribution(mean, std);		   // distribution of historical incomes
		
		for (int i = 0; i< this.getMemory(); i++) {
			probability = normal.cumulativeProbability(this.IncomeHistory.get(i));
			prob_delta_pos = normal.cumulativeProbability(this.IncomeHistory.get(i) + 1);
			prob_delta_neg = normal.cumulativeProbability(this.IncomeHistory.get(i) - 1);
			current_satisfaction.add(calculateSatisfaction(this.IncomeHistory.get(i),probability,prob_delta_pos,prob_delta_neg ));
		}
				
		return mean(current_satisfaction);
	}
	/** 
	 * Return mean value of provided list 
	 * @param list :: list of values to calculate mean with
	 * @return mean :: mean value of list
	 */
	private double mean(List<Double> list) {
		double mean = 0;												       // mean value to return
		
		for (int i = 0; i<list.size(); i++) {
			mean = mean + list.get(i);
		}
		
		mean  = mean / list.size();
		return mean;
	}
	/**
	 * This function calculates the standard deviation of provided list.
	 * @param list :: list for calculating standard deviation
	 * @return std :: standard deviation value
	 */
	private double std(List<Double> list) {
		double std = 0;		
		for (int i=0; i<list.size();i++)
		{
		    std = std + Math.pow(list.get(i) - mean(list), 2);
		}
		
		std = Math.sqrt(std/list.size());
		return std;
	}
	
	/** 
	 * Initialize a value for learning rate based on the memory limit of each farm
	 * @return learning_r :: value of learning rate
	 */
	public double init_learning_rate() {
		double max_edu = 1;
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
			k_upper = ln_ratio/( Math.round(memory_limit * m1_ratio) * max_edu);
			ln_ratio = -Math.log((1-lower_q)/lower_q);
			k_lower = ln_ratio/( Math.round(memory_limit * m2_ratio) * max_edu);
			upper_q = upper_q - delta;
			lower_q = lower_q + delta;
		}

		double learning_r = (k_upper + k_lower) / 2.0;		
		return learning_r;
	}
	/** 
	 * So if memory length is 5, we calculate an experience value for years 1 to 5. And using this set of experience values we calculate a standard deviation. <br>
	 * Given a specific value for k/learning_rate, calculate all possible q (experience value) for all possible memory lengths. <br>
	 * So if memory is 5 years long, we calculate a q value for years 1 to 5. And using this set of q values we calculate a standard deviation. <br>
	 * This standard deviation is used to set the upper and lower values for the q range. 
	 * @return q_range :: lower and upper boundary for fuzzy logic selection
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
	public void setActivity_Dissimilarity(double activity_dissimilarity) {
		this.Activity_Dissimilarity = activity_dissimilarity;
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
	public double getActivity_Dissimilarity() {
		return this.Activity_Dissimilarity;
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
	public void setCurrentActivity(List<Activity> activities) {
		this.currentActivity = activities;
	}
	public List<Activity> getCurrentActivity() {
		return this.currentActivity;
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
	public double getIncome_Dissimilarity() {
		return this.Income_Dissimilarity;
	}
	public void setIncome_Dissimilarity(double income_dissimilarity) {
		this.Income_Dissimilarity = income_dissimilarity;
	}
	public double getAveragePopulationIncomeChangeRate() {
		return this.averagePopulationIncomeChangeRate;
	}
	public void setAveragePopulationIncomeChangeRate(double averagePopulationIncomeChangePercent) {
		this.averagePopulationIncomeChangeRate = averagePopulationIncomeChangePercent;
	}
	public double getAveragePersonalIncomeChangeRate() {
		return this.averagePersonalIncomeChangeRate;
	}
	public void setAveragePersonalIncomeChangeRate(double averagePersonalIncomeChangeRate) {
		this.averagePersonalIncomeChangeRate = averagePersonalIncomeChangeRate;
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
	public void setLearningRate(double learning_rate) {
		this.learning_rate = learning_rate;
	}
	/** 
	 * If we want, we can set a learning rate based on the memory length
	 */
	public void calculate_setLearningRate() {
		this.learning_rate = init_learning_rate();
	}
	public double getP_beta_l() {
		return this.p_beta_l;
	}
	public void setP_beta_l(double p_beta_l) {
		this.p_beta_l = p_beta_l;
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
	public double getP_activity_tolerance_coef() {
		return p_activity_tolerance_coef;
	}
	public void setP_activity_tolerance_coef(double p_activity_tolerance_coef) {
		this.p_activity_tolerance_coef = p_activity_tolerance_coef;
	}
	public double getP_income_tolerance_coef() {
		return this.p_income_tolerance_coef;
	}
	public void setP_income_tolerance_coef(double p_income_tolerance_coef) {
		this.p_income_tolerance_coef = p_income_tolerance_coef;
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
	public double getP_beta_p() {
		return p_beta_p;
	}
	public void setP_beta_p(double p_beta_p) {
		this.p_beta_p = p_beta_p;
	}
	public double getP_reference_income() {
		return p_reference_income;
	}
	public void setP_reference_income(double p_reference_income) {
		this.p_reference_income = p_reference_income;
	}
	public double getP_opt_fuzzy_size() {
		return p_opt_fuzzy_size;
	}
	public void setP_opt_fuzzy_size(double p_fuzzy_size) {
		this.p_opt_fuzzy_size = p_fuzzy_size;
	}
	public double getP_imt_fuzzy_size() {
		return p_imt_fuzzy_size;
	}
	public void setP_imt_fuzzy_size(double p_imt_fuzzy_size) {
		this.p_imt_fuzzy_size = p_imt_fuzzy_size;
	}
	public int getP_ranking_version() {
		return p_ranking_version;
	}
	public void setP_ranking_version(int p_ranking_version) {
		this.p_ranking_version = p_ranking_version;
	}
}
