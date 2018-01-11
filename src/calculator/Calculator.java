package calculator;

import java.util.ArrayList;
import java.util.List;


import agent.farm.Farm;
import product.Crop;
import product.Livestock;

public class Calculator {

	public double getTransactionCost(Farm farm, List<Crop> crops, List<Livestock> livestock, List<Farm> farms) {
		
		List<Double> Q = new ArrayList<Double>();                              // learning by doing vector for specific farm
		List<Double> P = new ArrayList<Double>();							   // rank of all product preferences for specific farm
		Integer[] R;                           				 			   	   // Product preference vector 
		double m = farm.getPreferences().getProductName().size();		           // number of products in system
		int time = 0;
		int k = 5;
		double q;
		
		for (int i = 0; i < m; i++) {
			time = farm.getExperience().farmProductValue(farm.getFarmName(), farm.getPreferences().getProductName().get(i) );
			q = 1 / ( 1 +  Math.exp( (-k*time) ));
			Q.add(q);
		}
		
		R = farm.getPreferences().getProductmap().get(farm.getFarmName());
		
		for (int i = 0; i< m; i++) {
			P.add(1 - R[i]/m);
		}
		
		
		
		return 0;
	}
	
	
	
	
}
