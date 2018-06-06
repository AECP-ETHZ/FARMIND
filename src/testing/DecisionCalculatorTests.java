package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import agent.Farm;
import decision.DecisionCalculator;
import reader.ReadData;		

/**
 * This class tests the decision calculator. 
 *
 */
public class DecisionCalculatorTests {
	List<Farm>     allFarms = new ArrayList<Farm>();
	
	@Before 
	public void setup() {
		ReadData reader = new ReadData();						   // read all input data files
		useTestData(reader);
		allFarms = reader.getFarms(1);						                   // build set of farms with new parameters
	}
	
	@Test
	public void testProductSelectionCalculator() {
		Farm farm = allFarms.get(0);
		DecisionCalculator cal = new DecisionCalculator(farm, allFarms);
		assertNotEquals(cal, null);
	}

	@Test
	public void testLearningVector() {
		Farm farm = allFarms.get(0);
		DecisionCalculator cal = new DecisionCalculator(farm, allFarms);
		double m = farm.getPreferences().getDataElementName().size();		       // number of products in system

		List<Double> Q = new ArrayList<Double>();                              // learning by doing vector for specific farm
		int time = 0;														   // years of experience
		double k = farm.getLearningRate();												   // scale factor
		double q;															   // calculated score
		
		for (int i = 0; i < m; i++) {
			time = farm.getExperience().getFarmDataElementValue(farm.getFarmName(), farm.getPreferences().getDataElementName().get(i) );
			q = 1 / ( 1 +  Math.exp( (-k*time) ));
			Q.add(q);
		}
		
		List<Double> L = cal.L;
		int l = L.size();
		L.remove(l-1);
		L.remove(l-2);                                                         // original L vector has q+,q- at end
		assertEquals(L,Q);
	}
	
	@Test
	public void testPreferenceVector() {
		Farm farm = allFarms.get(0);
		DecisionCalculator cal = new DecisionCalculator(farm, allFarms);
		double m = farm.getPreferences().getDataElementName().size();		       // number of products in system
		List<Double> P = new ArrayList<Double>();							   // rank of all product preferences for specific farm
		Integer[] R;                           				 			   	   // Product preference vector 

		R = farm.getPreferences().getFarmMap().get(farm.getFarmName());
		
		for (int i = 0; i< m; i++) {
			P.add(1 - R[i]/m);
		}
		
		List<Double> Px = cal.P;
		int l = Px.size();
		Px.remove(l-1);
		Px.remove(l-2); 
		
		assertEquals(Px,normalizeList(P));
	}

	@Test
	public void testNDSelection() {
		Farm farm = allFarms.get(0);
		DecisionCalculator cal = new DecisionCalculator(farm, allFarms);
		List<Double> crit1 = Arrays.asList(15.0,10.0,5.0,1.0,5.0);	           // sample from document
		List<Double> crit2 = Arrays.asList(6.0,14.0,10.0,1.0,5.0);
		List<Double> crit3 = Arrays.asList(10.0,7.0,13.0,1.0,5.0);
		cal.L = crit1;
		cal.P = crit2;
		cal.S = crit3;
		cal.getImitationActivities();
		
		List<Double> verify = Arrays.asList(0.345, 1.0, 0.039999999999999925); //Arrays.asList(0.925, 0.8500000000000001, 0.7750000000000001);
		List<Double> x = cal.ND;
		assertEquals(x,verify);
		
	}

	
	// helper functions
	public static final String TestDataFile = "./test_data/farm_data.csv";
	public static final String TestParameterFile = "./test_data/parameters.csv";
	public static final String TestPreferenceFile = "./test_data/products_preference.csv";
	public static final String TestYearsFile = "./test_data/farming_years.csv";
	public static final String TestSocialNetworkFile = "./test_data/social_networks.csv";
	public static final String TestActivityFile = "./test_data/activities.csv";
	
	private void useTestData(ReadData reader) {
			reader.DataFile = TestDataFile;
			reader.ParameterFile = TestParameterFile;
			reader.PreferenceFile = TestPreferenceFile;
			reader.YearsFile = TestYearsFile;
			reader.SocialNetworkFile = TestSocialNetworkFile;
			reader.ActivityFile = TestActivityFile;
	} 
	
	/**
	 * Create a normalized list from 0 to 1
	 * @param list unormalized list
	 * @return normalized list
	 */
	private List<Double> normalizeList(List<Double> list) {
		List<Double> normalizedList = new ArrayList<Double>();				   // normalized list to return

		double min = min(list);
		double max = max(list);
		
		for (int i = 0; i<list.size();i++) {
			 normalizedList.add( (list.get(i) - min) / (max - min) );
		}
		return normalizedList;
	}
	
	/** 
	 * Find minimum of List of doubles
	 * @param list of input values
	 * @return minimum value
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
	 * Find max of List of doubles
	 * @param list of input values
	 * @return max value
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
