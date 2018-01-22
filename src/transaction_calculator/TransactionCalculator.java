package transaction_calculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;

import agent.farm.Farm;
import product.Crop;
import product.Livestock;

/** 
 * Object contains three vectors (Q,P,S) that contain normalized rankings of experience, preference, and social network experience for a specific farm. 
 * 
 * Create the calculator for each individual farm, and the calculator can be used to decide on the imitation and optimization actions
 * 
 * @author kellerke
 *
 */
public class TransactionCalculator {
	List<Double> Q = new ArrayList<Double>();                                  // learning by doing vector for specific farm
	List<Double> P = new ArrayList<Double>();							       // rank of all product preferences for specific farm
	List<Double> S = new ArrayList<Double>();							       // average social learning value for each products weighted by social network
	Farm farm;																   // farm associated with this calculator 

	public TransactionCalculator(Farm farm, List<Farm> farms) {
		double m = farm.getPreferences().getProductName().size();		       // number of products in system
	
		this.Q = getFarmExperienceVector(farm,m);
		this.P = getFarmPreferenceVector(farm,m);
		this.S = getNetworkExperienceAverageVector(farm, m, farms);
		this.farm = farm;
	}
	
	/** 
	 * Using fuzzy logic check S,P,Q lists to determine best product combinations
	 * @return
	 */
	public List<String> getImitationProducts() {
		
		Q.add(0.4);
		Q.add(0.8);
		double[] c1 = new double[this.Q.size()];
		for (int i = 0; i < this.Q.size(); i++) {
			c1[i] = Q.get(i);
		}
		
		P.add(0.4);
		P.add(0.8);
		double[] c2 = new double[this.P.size()];
		for (int i = 0; i < this.P.size(); i++) {
			c2[i] = P.get(i);
		}
		
		S.add(0.4);
		S.add(0.8);
		double[] c3 = new double[this.S.size()];
		for (int i = 0; i < this.S.size(); i++) {
			c3[i] = S.get(i);
		}
		
		double[][] p1 = preference_matrix(c1);
		double[][] p2 = preference_matrix(c2);
		double[][] p3 = preference_matrix(c3);
		
		int len = c1.length;
		double[][] matrix = new double[len-2][len-2];
		 
		for (int i = 0; i< len - 2; i++) {
			for (int j = 0; j < len - 2; j++) {
				matrix[i][j] = (p1[i][j] + p2[i][j] + p3[i][j] ) / 3;
			}
		}
		
		List<Double> ND = new ArrayList<Double>();                             // non-domination score vector to apply for clustering
		for (int i = 0; i< len - 2; i++) {
			ND.add(ND(i,matrix));
		}

		List<String> list = productList(ND);							       // cluster algorithm returns optimal product list
		
		return list;
	}
	
	/** 
	 * Using fuzzy logic check P,Q lists to determine best product combinations.
	 * Do not take into account social learning vector S
	 * @return
	 */
	public List<String> getOptimizeProducts() {
		Q.add(0.4);
		Q.add(0.8);
		double[] c1 = new double[this.Q.size()];
		for (int i = 0; i < this.Q.size(); i++) {
			c1[i] = Q.get(i);
		}
		
		P.add(0.4);
		P.add(0.8);
		double[] c2 = new double[this.P.size()];
		for (int i = 0; i < this.P.size(); i++) {
			c2[i] = P.get(i);
		}
		
		double[][] p1 = preference_matrix(c1);
		double[][] p2 = preference_matrix(c2);
		
		int len = c1.length;
		double[][] matrix = new double[len-2][len-2];
		 
		for (int i = 0; i< len - 2; i++) {
			for (int j = 0; j < len - 2; j++) {
				matrix[i][j] = (p1[i][j] + p2[i][j]  ) / 2;
			}
		}
		
		List<Double> ND = new ArrayList<Double>();                             // non-domination score vector to apply for clustering
		for (int i = 0; i< len - 2; i++) {
			ND.add(ND(i,matrix));
		}
		
		List<String> list = productList(ND);                                   // cluster algorithm returns optimal product list

		return list;
	}

	/**
	 *  given a vector of non-domination scores return an optimized product list
	 *  using a clustering algorithm to detect two groups within the list
	 * @param x original ND vector
	 * @return list of product names
	 */
	private List<String> productList(List<Double> x){
		List<String> list = new ArrayList<String>();
		List<Double> original = new ArrayList<Double>();
		List<Double> sorted = new ArrayList<Double>();
		original = x;
		sorted = x;
		Collections.sort(sorted);
		
		List<Double> cluster = new ArrayList<Double>();
		cluster = cluster(sorted);
		
		int index =0;
		for (int i = 0; i< cluster.size(); i++) {
			index = original.indexOf(cluster.get(i));
			list.add(this.farm.getPreferences().getProductName().get(index));
			original.set(index, -1.0);                                         // duplicate values exist in array, so 'remove' when used to get next duplicate value
		}
		
		return list;
	}
	
	/** 
	 * K-means clustering with 2 partitions
	 * @param sorted original list to cluster
	 * @return list of prefered products
	 */
	private List<Double> cluster(List<Double> sorted) {
		double x1 = 0.75;                                                      // initial cluster
		double x2 = 0.25;
		double x1_old = 0;
		double x2_old = 0;
		double dist1, dist2 = 0.0;
		List<Double> cluster1 = new ArrayList<Double>(); 
		List<Double> cluster2 = new ArrayList<Double>(); 
		
		while ((x1 != x1_old) && (x2 != x2_old))	{					       // when the cluster means don't change we have converged
			cluster1.clear();
			cluster2.clear();
			for (int i = 0; i < sorted.size(); i++) {
				dist1 = Math.abs(sorted.get(i) - x1);
				dist2 = Math.abs(sorted.get(i) - x2);
				
				if (dist1 < dist2) {										   // apply two clusters to list based on mean score
					cluster1.add(sorted.get(i));
				}
				else if (dist1 >= dist2) {
					cluster2.add(sorted.get(i));
				}
			}
			x1_old = x1;													   // keep old mean to compare when we converge
			x2_old = x2;
			x1 = mean(cluster1);
			x2 = mean(cluster2);
		}
		return cluster1;													   // higher value cluster is optimal product list values
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
	 * Non Domination score for an 
	 * @param index which item in the list (eg product) we want to score against the criterion matrix
	 * @param matrix of criteria preferences for all items
	 * @return nd score for this product
	 */
	private double ND(int index, double[][] matrix ) {
		List<Double> ND = new ArrayList<Double>();
		
		for (int j = 0; j < matrix[0].length; j++) {
			if (index != j) {
				double x = matrix[j][index];
				double y = matrix[index][j];
				ND.add( (x - y) );
			}
		}	
		return 1 - max(ND);
	}
	
	/** 
	 * Build preference matrix for a catagory based on the t score and the q range
	 * @param catagory
	 * @return matrix of preferences
	 */
	private double[][] preference_matrix(double[] catagory) {
		int len = catagory.length;
		double q_plus = catagory[len-1];
		double q_minus = catagory[len-2];
		double[][] matrix = new double[len-2][len-2];
		
		for (int i = 0; i< len-2; i++) {
			for (int j = 0; j < len - 2; j++) {
				matrix[i][j] = t_rating(catagory[i],catagory[j], q_minus,q_plus);
			}
		}
		
		return matrix;
	}
	
	/** 
	 * Build a rating score between x,y based on the q range
	 * @param x
	 * @param y
	 * @param q_minus
	 * @param q_plus
	 * @return rating between x and y
	 */
	private double t_rating(double x, double y, double q_minus, double q_plus) {
		double rank = 0;
		
		if ( (x - y) > q_plus) rank = 1;
		else if( (x - y) <= q_minus ) rank = 0;
		else {
			rank = ( (x - y - q_minus)/ (q_plus - q_minus));
		}
		
		return rank;
	}
		
	/** 
	 * Each farm has a vector with associated years of experience in the shared matrix of experience.
	 * Scale this vector based on a parameter k. 
	 * @param farm
	 * @param m
	 * @return
	 */
	private List<Double> getFarmExperienceVector(Farm farm, double m) {
		List<Double> Q = new ArrayList<Double>();                              // learning by doing vector for specific farm
		int time = 0;
		double k = 0.6;
		double q;
		
		for (int i = 0; i < m; i++) {
			time = farm.getExperience().getFarmProductValue(farm.getFarmName(), farm.getPreferences().getProductName().get(i) );
			q = 1 / ( 1 +  Math.exp( (-k*time) ));
			Q.add(q);
		}

		return normalizeList(Q);
	}
	
	/** 
	 * Each farm has a preference vector and we scale this vector based on the number of products in the system
	 * @param farm
	 * @param m
	 * @return
	 */
	private List<Double> getFarmPreferenceVector(Farm farm, double m) {
		List<Double> P = new ArrayList<Double>();							   // rank of all product preferences for specific farm
		Integer[] R;                           				 			   	   // Product preference vector 

		R = farm.getPreferences().getProductmap().get(farm.getFarmName());
		
		for (int i = 0; i< m; i++) {
			P.add(1 - R[i]/m);
		}
		
		return normalizeList(P);
	}
	
	/** 
	 * for all farms in the network connected to the main farm build the Experience vector (Q). 
	 * Then take the average experience level for each product for all farms in the network 
	 * Base this average on the social network weight between the main farm and the node farm
	 * 
	 * @param farm
	 * @param m
	 * @return
	 */
	private List<Double> getNetworkExperienceAverageVector(Farm farm, double m, List<Farm> farms) {
		List<Double> S = new ArrayList<Double>();							   // rank of all product preferences for specific farm

		int i,j = 0;	
        double totalFarms = 0;													// how many total farms are there in the network
		Set<DefaultEdge> E;
		Iterator<DefaultEdge> I;
        double w;
        double sum = 0;
        		
		// social learning calculation
		E = farm.getNetwork().outgoingEdgesOf(farm.getFarmName());
        totalFarms = farms.size();
        I = E.iterator();

        List<List<Double>> QForAllFarms = new ArrayList<List<Double>>();
        
        for (i = 0; i < totalFarms; i++) {
        	if (!farms.get(i).getFarmName().equals(farm.getFarmName()) ) {
        		w = farm.getNetwork().getEdgeWeight(I.next());						   // weight of social tie between main farm and farm i
        		List<Double> Q = new ArrayList<Double>();                              // learning by doing vector for farm i
        		List<Double> Q_scaled = new ArrayList<Double>();                       // scaled learning by doing vector for farm i

        		Q = getFarmExperienceVector(farms.get(i), m);
        		for (j = 0; j< m; j++) {
        			 Q_scaled.add(Q.get(j)*w);
        		}

        		QForAllFarms.add(normalizeList(Q_scaled));
        	}
        }
        
        // loop through all products and all farms to develop final list
        for (j = 0; j < m; j++) {
        	sum = 0;
        	for (i = 0; i < QForAllFarms.size(); i++) {
        		if (QForAllFarms.get(i).get(j) > 0) {
        			sum = sum + QForAllFarms.get(i).get(j);                        // farm i, product j
        		}
        	}
        	sum = sum/QForAllFarms.size();
        	S.add(sum);
        }

		return normalizeList(S);
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
	
	private List<Double> normalizeList(List<Double> list) {
		List<Double> normalizedList = new ArrayList<Double>();

		double min = min(list);
		double max = max(list);
		
		for (int i = 0; i<list.size();i++) {
			 normalizedList.add( (list.get(i) - min) / (max - min) );
		}
		return normalizedList;
	}
	
	private double min(List<Double> list) {
		double min = 1;
		double temp = 0;
		
		for(int i=0; i<list.size();i++) {
			temp = list.get(i);
			if (temp < min) { min = temp;}
		}
		return min;
	}
	
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
