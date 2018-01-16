package calculator;

import java.util.ArrayList;
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
	List<Double> Q = new ArrayList<Double>();                              // learning by doing vector for specific farm
	List<Double> P = new ArrayList<Double>();							   // rank of all product preferences for specific farm
	List<Double> S = new ArrayList<Double>();							   // average social learning value for each products weighted by social network

	public TransactionCalculator(Farm farm, List<Crop> crops, List<Livestock> livestock, List<Farm> farms) {
		double m = farm.getPreferences().getProductName().size();		       // number of products in system
	
		this.Q = getFarmExperienceVector(farm,m);
		this.P = getFarmPreferenceVector(farm,m);
		this.S = getNetworkExperienceAverageVector(farm, m, farms);
	}
	
	private List<Double> getFarmExperienceVector(Farm farm, double m) {
		List<Double> Q = new ArrayList<Double>();                              // learning by doing vector for specific farm
		int time = 0;
		int k = 1;
		double q;
		
		for (int i = 0; i < m; i++) {
			time = farm.getExperience().getFarmProductValue(farm.getFarmName(), farm.getPreferences().getProductName().get(i) );
			q = 1 / ( 1 +  Math.exp( (-k*time) ));
			Q.add(q);
		}

		return normalizeList(Q);
	}
	
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
		double min = 0;
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
