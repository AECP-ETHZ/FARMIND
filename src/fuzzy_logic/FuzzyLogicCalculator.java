package fuzzy_logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;
import agent.Farm;

/** 
 * Object contains three vectors (L,P,S) that contain normalized rankings of experience, preference, and social network experience for a specific farm. <br>
 * Create the calculator for each individual farm, and the calculator can be used to decide on the imitation and optimization activities using fuzzy preference. <br>
 * <br>
 *  q range shows how different two activities can be based on the criteria and still be 1) equivalent, 2) weighted, 3) not equivalent<br>
 *  If you take a criteria vector = [0.9, 0.1, 0.8, 0.7, 0.4] that corresponds to activities [A, B, C, D, E] and<br>
 *  if we set the lower bound q- to 0.3 and the upper bound q+ to 0.6 we get the following difference vector <br>
 *  for A compared to all other activities (one directional comparison):<br>
 *  delta = [0, 0.8, 0.1, 0.2, 0.5] ie 0.5 corresponds to A-E :: 0.9-0.4 = 0.5 <br>
 *  We can now build another vector of comparison values for A compared to all other activities based on the q ranges: <br>
 *  CP = [0, 1, 0, 0, 2/3] which shows that when activity A is compared to all other activities in the set which shows<br>
 *  Product A is strictly preferred to Product B, and weakly preferred to activity E. It is not preferred at all to C or D. <br>
 * 
 * @author kellerke
 */
public class FuzzyLogicCalculator {
	
	public List<Double> L = new ArrayList<Double>();                           // learning by doing vector for specific farm 
	public List<Double> P = new ArrayList<Double>();						   // rank of all activity preferences for specific farm 
	public List<Double> S = new ArrayList<Double>();						   // average social learning value for each activities weighted by social network 
	public List<Double> ND = new ArrayList<Double>();                          // non-domination score vector to apply for clustering 
	Farm farm;																   // farm associated with this calculator 
	double fuzzy_size = 0;									  		           // set size of set to return
	int ranking_version = 0;							                       // what ranking version to use

	/** 
	 * Constructor for decision calculator for a specific farm based on the network.
	 * @param farm :: specific farm to study
	 * @param farms :: list of all farms in the region for network information
	 */
	public FuzzyLogicCalculator(Farm farm, List<Farm> farms) {
		double m = farm.getPreferences().getDataElementName().size();		   // number of activities in system
		this.ranking_version = farm.getP_ranking_version();                     // what ranking version to use
		
		this.L = getFarmExperienceVector(farm,m);
		this.S = getNetworkExperienceAverageVector(farm, m, farms);
		this.P = getFarmPreferenceVector(farm,m);
		this.farm = farm;
		farm.setQ_range();
		double q_minus = farm.getQ_range().get(0);							   // set upper and lower q range for experience
		double q_plus  =  farm.getQ_range().get(1);  
		L.add(q_minus);															  
		L.add(q_plus);														   

		q_minus = 0.4;														   // set upper and lower q range for preference
		q_plus  = 0.6;  
		P.add(q_minus);														   
		P.add(q_plus);	
		
		q_minus = 0.4;														   // set upper and lower q range for preference
		q_plus  = 0.6;
		S.add(q_minus);
		S.add(q_plus);
	}
	
	// activity calculations
	/** 
	 * Using fuzzy logic check S,P,L lists to determine best activity combinations. 
	 * The len-2 part of the calculation is to account for q+ and q- minus at the start and end of the calculation
	 * @return list of activities from the activity selection calculator
	 */
	public List<String> getImitationActivities() {
		double[] c1 = new double[this.P.size()];
		int len = c1.length;
		double[][] matrix = new double[len-2][len-2];                          // matrix of all activities against all activities
		
		this.fuzzy_size = this.farm.getP_imt_fuzzy_size();					   // use imitation fuzzy size for this selection
		
		for (int i = 0; i < this.P.size(); i++) {
			c1[i] = P.get(i);
		}
		
		double[] c2 = new double[this.L.size()];	
		for (int i = 0; i < this.L.size(); i++) {
			c2[i] = L.get(i);
		}
				
		double[] c3 = new double[this.S.size()];
		for (int i = 0; i < this.S.size(); i++) {
			c3[i] = S.get(i);
		}
		
		double[][] p_p = preference_matrix(c1);
		double[][] p_l = preference_matrix(c2);
		double[][] p_s = preference_matrix_social_network(c3);
		
		for (int i = 0; i< len - 2; i++) {
			for (int j = 0; j < len - 2; j++) {
				double score = ( farm.getP_beta_p()*p_p[i][j] + farm.getP_beta_l()*(p_l[i][j]) + farm.getP_beta_s()*(p_s[i][j]) )  / (p_p[i][j] + (p_l[i][j]) + (p_s[i][j]));
				if(Double.isNaN(score)) {
					score = 0;
				}
				matrix[i][j] = score ;
			}
		}
		
		for (int i = 0; i< len - 2; i++) {
			ND.add(ND(i,matrix));											   // add ordered values to list
		}

		List<String> list = activityList((ND));							       // cluster algorithm returns optimal activity list
		
		return list;
	}
	/** 
	 * Using fuzzy logic check P, L lists to determine best activity combinations.
	 * Do not take into account social learning vector S. 
	 * The len-2 part of the calculation is to account for q+ and q- minus at the start and end of the calculation
	 * @return list of activities from the activity selection calculator
	 */
	public List<String> getOptimizationActivities() {
		double[] c1 = new double[this.P.size()];							   
		double[] c2 = new double[this.L.size()];
		int len = c1.length;
		double[][] matrix = new double[len-2][len-2];						   // matrix of all activities against all activities
		
		this.fuzzy_size = this.farm.getP_opt_fuzzy_size();					   // for optimization activities we use this fuzzy size
		
		for (int i = 0; i < this.P.size(); i++) {
			c1[i] = P.get(i);
		}
		
		for (int i = 0; i < this.L.size(); i++) {
			c2[i] = L.get(i);
		}
		
		double[][] p_p = preference_matrix(c1);
		double[][] p_l = preference_matrix(c2);
		
		for (int i = 0; i< len - 2; i++) {
			for (int j = 0; j < len - 2; j++) {
				double score = ( farm.getP_beta_p()*p_p[i][j] + farm.getP_beta_l()*p_l[i][j])  / (p_p[i][j] + p_l[i][j]);
				if(Double.isNaN(score)) {
					score = 0;
				}
				matrix[i][j] = score ;
			}
		}
		
		for (int i = 0; i< len - 2; i++) {
			ND.add(ND(i,matrix));                                              // add ordered values to list
		}
		
		List<String> list = activityList(ND);                                   // cluster algorithm returns optimal activity list
		
		return list;
	}

	// fuzzy logic and clustering functions
	/**
	 *  given a vector of non-domination scores (doubles) return an optimized activity list based on the desired fuzzy set size OR the natural clustering
	 * @param x :: original ND vector
	 * @return activityList :: list of activity names
	 */
	private List<String> activityList(List<Double> x){
		List<String> activityList = new ArrayList<String>();                   // final activity list
		List<Double> originalND = new ArrayList<Double>(x);					   // backup list					
		List<Double> sortedND = new ArrayList<Double>(x);					   // sorted list
		List<Double> cluster = new ArrayList<Double>();						   // final selected activity values from clustering algorithm
		int index = 0;														   // index of ND value used to get specific activity indicated by index
		
		Collections.sort(sortedND); 										   // returns ascending order 0->1
		cluster = activityOptimalSelection(sortedND);
		
		// turn ranking values into activity list by sorting the ND array, and then finding the corresponding match in the original list based on the index.
		// Ex: orig = [0.8,0.3,1.0,0.8] --> sorted = [1.0,0.8,0.8,0.3]
		// Take 1.0 from the sorted list and match to index value 2 from the original list. Then find activity with index 2 in the activity list of the system
		// Then take 0.8 and match that to index 0 and find corresponding activity[index = 0].
		for (int i = 0; i< cluster.size(); i++) {							   
			index = originalND.indexOf(cluster.get(i));
			activityList.add(this.farm.getPreferences().getDataElementName().get(index));
			originalND.set(index, -1.0);                                         // duplicate values exist in array, so 'remove' when used to get next duplicate value
		}
		
		return activityList;
	}
	
	/** 
	 * if there are 1.0 values in the sorted list, then we have optimal values to return. If there are no 1.0 values then we select the best values based on the fuzzy size. <br>
	 * a list of [1.0, 0.8, 1.0, 0.9, 0.2, 0.3] will return [1.0,1.0].
	 * a list of [0.9, 0.8, 0.63, 0.9, 0.2, 0.3] will return [0.9,0.9,0.8] assuming fuzzy set size of 3. 
	 * @param sorted :: original list to cluster
	 * @return list of preferred activity scores
	 */
	private List<Double> activityOptimalSelection(List<Double> sorted) {
		List<Double> cluster = new ArrayList<Double>(); 
		List<Double> cluster_smaller = new ArrayList<Double>(); 
		int fuzzy_size = 0;
		
		if (farm.getP_ranking_version() == 0) {
			for (int i = 0; i< sorted.size(); i++) {		
				if (sorted.get(i) == 1.0) {
					fuzzy_size++;
				}
			}
			
			// if no 1.0 optimal activities are present than use the default size and select the best option. 
			if (fuzzy_size == 0) {
				fuzzy_size = (int) this.fuzzy_size;	
			}
		}
		else if (farm.getP_ranking_version() == 1 ) {
			fuzzy_size = (int) this.fuzzy_size;		
		}
			
		cluster = sorted;
		if (cluster.size() > fuzzy_size) {											   
			for (int i = 0; i< fuzzy_size; i++) {
				cluster_smaller.add(cluster.get(cluster.size() - i - 1));
			}
			return cluster_smaller;
		}	
		
		return cluster;		
	}
		
	/** 
	 * Non Domination score for a fuzzy logic preference matrix.
	 * Calculate ND score by comparing activities 'index' against all other activities
	 * @param index :: which item in the list (eg activity) we want to score against the criterion matrix
	 * @param matrix :: criteria preferences for all items
	 * @return nd score for this activity
	 */
	private double ND(int index, double[][] matrix ) {
		List<Double> ND = new ArrayList<Double>();                             // set of ND scores for activity 'index' against all other activities
		
		for (int j = 0; j < matrix[0].length; j++) {
			if (index != j) {
				double x = matrix[j][index];
				double y = matrix[index][j];
				ND.add( (x - y) );
			}
		}	
		double maxND = max(ND);
		if(maxND < 0) maxND = 0;
		
		return 1-maxND; 
	}
	
	/** 
	 * Build preference matrix for a category based on the t score and the q range
	 * @param category :: input list of rated values. In this case L,S,P vectors
	 * @return matrix :: matrix of preferences comparing each activity against all others
	 */
	private double[][] preference_matrix(double[] category) {
		int len = category.length;											// length of matrix columns/rows
		double q_plus = category[len-1];									// set q-
		double q_minus = category[len-2];								    // set q+ 
		double[][] matrix = new double[len-2][len-2];					    // cross activity preference matrix
		
		for (int i = 0; i< len-2; i++) {
			for (int j = 0; j < len - 2; j++) {
				matrix[i][j] = t_rating(category[i],category[j], q_minus,q_plus);
			}
		}
		
		return matrix;
	}
	
	/** 
	 * Build preference matrix for the social network category based on the t score and the q range
	 * @param category :: input list of rated values. In this case L,S,P vectors
	 * @return matrix :: matrix of preferences comparing each activity against all others
	 */
	private double[][] preference_matrix_social_network(double[] category) {
		int len = category.length;											// length of matrix columns/rows
		double q_plus = category[len-1];									// set q-
		double q_minus = category[len-2];								    // set q+ 
		double[][] matrix = new double[len-2][len-2];					    // cross activity preference matrix
		
		for (int i = 0; i< len-2; i++) {
			for (int j = 0; j < len - 2; j++) {
				double x = category[i];
				double y = category[j];
				if (x > 0) { x = 1.0;}                                         // set to 1.0 when using social network to enforce stricter boundaries
				if (y > 0) { y = 1.0;}										   // this is to ensure that nothing		
				matrix[i][j] = t_rating(x,y, q_minus,q_plus);
			}
		}
		
		return matrix;
	}
	
	/** 
	 * Build a rating score between x,y based on the q range
	 * @param x :: first item value
	 * @param y :: second item value
	 * @param q_minus :: lower range of fuzzy region in set
	 * @param q_plus :: upper range of fuzzy region in set
	 * @return rank :: rating between x and y (not y and x)
	 */
	private double t_rating(double x, double y, double q_minus, double q_plus) {
		double rank = 0;
		
		if ( (x - y) > q_plus) {
			rank = 1;
		}
		else if( (x - y) <= q_minus ) rank = 0;
		else {
			rank = ( (x - y - q_minus)/ (q_plus - q_minus));
		}
		
		return rank;
	}
		
	/** 
	 * Each farm has a vector with associated years of experience in the shared matrix of experience.
	 * Scale this vector based on a parameter k. 
	 * @param farm :: agent that we are performing the calculations on
	 * @param m :: number of activities in the system
	 * @return L :: vector of farming preference for this farm
	 */
	private List<Double> getFarmExperienceVector(Farm farm, double m) {
		List<Double> L = new ArrayList<Double>();                              // learning by doing vector for specific farm
		double time = 0;													   // years of experience
		double k = farm.getLearningRate();								       // scale factor
		double score;														   // calculated score
		
		for (int i = 0; i < m; i++) {
			time = farm.getExperience().getFarmDataElementValue(farm.getFarmName(), farm.getPreferences().getDataElementName().get(i) );
			score = 1 / ( 1 +  Math.exp( (-k*time) ));
			L.add(score);
		}

		return normalizeList(L);
	}
	
	/** 
	 * Each farm has a preference vector and we scale this vector based on the number of activities in the system. 
	 * The vector contains values between 1 and 5 that correspond to the preference of the farmer for that specific activity with 1 being most preferred and 5 being least preferred. 
	 * @param farm :: agent that we are performing the calculations on
	 * @param m :: number of activities in the system
	 * @return P :: vector of farming preference for this farm
	 */
	private List<Double> getFarmPreferenceVector(Farm farm, double m) {
		List<Double> P = new ArrayList<Double>();							   // rank of all activity preferences for specific farm
		Double[] R;                           				 			   	   // Product preference vector 

		R = farm.getPreferences().getFarmMap().get(farm.getFarmName());
		
		for (int i = 0; i< m; i++) {
			//P.add(1 - R[i]/m);												   // this (1 - x/m) ensures that if x=1 (most preferred), we will return value closest to 1. And x=5 will be smaller
			P.add(R[i]);
		}
			
		return normalizeList(P);
	}
	
	/** 
	 * For all farms in the network connected to the main farm build the Experience vector (L). 
	 * Then weight this vector based on the social network weight between the main farm and this connected node farm
	 * For the final Network Experience vector take the average experience level for each activity for all farms in the network 
	 * @param farm :: specific agent that we are performing the calculations on
	 * @param allFarms :: all farms in the system 
	 * @param m :: number of activities in the system
	 * @return S :: vector of social farming experience for this farm
	 */
	private List<Double> getNetworkExperienceAverageVector(Farm farm, double m, List<Farm> allFarms) {
		List<Double> S = new ArrayList<Double>();							   // rank of all activity preferences for specific farm
		int i,j = 0;														   // iterators
        double totalFarms = 0;												   // how many total farms are there in the network
		Set<DefaultEdge> E;													   // set of edges in the network
		Iterator<DefaultEdge> I;											   // iterator through all edges
        double w;															   // weight of edge between two nodes
        double sum = 0;														   // running sum of activity score
        List<List<Double>> L_forAllFarms = new ArrayList<List<Double>>();      // aggregate list of all experience vectors where L is the experience vector for an individual farm

		// social learning calculation
		E = farm.getNetwork().outgoingEdgesOf(farm.getFarmName());
        totalFarms = allFarms.size();
        I = E.iterator();

        for (i = 0; i < totalFarms; i++) {
        	if (!allFarms.get(i).getFarmName().equals(farm.getFarmName()) ) {
        		w = farm.getNetwork().getEdgeWeight(I.next());				   // weight of social tie between main farm and farm i
        		if (w > 0) {
	        		List<Double> L = new ArrayList<Double>();                  // learning by doing vector for farm i
	        		List<Double> L_scaled = new ArrayList<Double>();           // scaled learning by doing vector for farm i
	
	        		L = getFarmExperienceVector(allFarms.get(i), m);
	        		for (j = 0; j< m; j++) {
	        			 L_scaled.add(L.get(j)*w);							   // weight specific years of experience based on social tie weight
	        		}
	
	        		L_forAllFarms.add((L_scaled));
        		}
        	}
        }
        
        // loop through all activities and all farms to develop final list
        for (j = 0; j < m; j++) {
        	sum = 0;
        	for (i = 0; i < L_forAllFarms.size(); i++) {
        		if (L_forAllFarms.get(i).get(j) > 0) {
        			sum = sum + L_forAllFarms.get(i).get(j);                   // farm i, activity j
        		}
        	}
        	sum = sum/L_forAllFarms.size();									   // normalize based on number of farms
        	S.add(sum);
        }

		return normalizeList(S);
	}
	
	/**
	 * Create a normalized list from 0 to 1
	 * @param list :: original list
	 * @return normalizedList :: normalized list based on input list
	 */
	private List<Double> normalizeList(List<Double> list) {
		List<Double> normalizedList = new ArrayList<Double>();				   // normalized list to return

		double min = min(list);
		double max = max(list);
		
		if (min == max) {													   // prevents NaN from being returned
			for (int i = 0; i<list.size();i++) {
				 normalizedList.add( 1.0 );
			}
		}
		else {
			for (int i = 0; i<list.size();i++) {
				 normalizedList.add( (list.get(i) - min) / (max - min) );
			}
		}
		
		return normalizedList;
	}
	
	/** 
	 * Find minimum of list of doubles
	 * @param list :: input values for finding minimum
	 * @return min :: minimum value of input list
	 */
	private double min(List<Double> list) {
		double min = 1;
		double temp = 0;
		
		for(int i=0; i<list.size();i++) {
			temp = list.get(i);
			if (temp < min) { min = temp;}
		}
		return min;
	}
	
	/** 
	 * Find max of list of doubles
	 * @param list :: input values for finding max
	 * @return max :: max value of input list
	 */
	private double max(List<Double> list) {
		double max = 0;
		double temp = 0;
		
		for(int i=0; i<list.size();i++) {
			temp = list.get(i);
			if (temp > max) { max = temp;}
		}
		return max;
	}
	
}
