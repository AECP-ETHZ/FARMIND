package testing;

import reader.ReadData;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
//import org.junit.jupiter.api.Test;

import activity.Activity;
import agent.Farm;

/** 
 * This class tests farm agent object.
 *
 */
public class FarmTests {
	List<Farm>     allFarms = new ArrayList<Farm>();
	
	@Before 
	public void setup() {
		ReadData reader = new ReadData();						               // read all input data files
		useTestData(reader);
		allFarms = reader.getFarms();						                   // build set of farms with new parameters
		
		main.Consumat.initializePopulationIncomeChangeRate(allFarms);

	}
	
	@Test
	public void testCreateFarm() {
		Farm farm = allFarms.get(0);
		assertNotEquals(farm, null);
	}
	
	@Test
	public void testAgeExitDecision() {
		Farm farm = allFarms.get(0);
		farm.getHead().setAge(700);
		farm.decideActivitySet(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 1);	// exit
	}
	
	@Test
	public void testImitationDecisionActivity() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(100);
		farm.setActivity_Dissimilarity(10);
		farm.setIncome_Dissimilarity(0);
		farm.decideActivitySet(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 2);	
	}
	
	@Test
	public void testImitationDecisionIncome() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(100);
		farm.setActivity_Dissimilarity(0);
		farm.setIncome_Dissimilarity(10);
		farm.decideActivitySet(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 2);	
	}
	
	@Test
	public void testRepetionDecision() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(100);
		farm.setActivity_Dissimilarity(0);
		farm.setIncome_Dissimilarity(0);
		farm.decideActivitySet(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 4);	
	}
	
	@Test
	public void testOptimizationDecision() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(-1);
		farm.setActivity_Dissimilarity(0);
		farm.setIncome_Dissimilarity(0);
		farm.decideActivitySet(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 3);	
	}
	
	@Test
	public void testOptOutDecision() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(-1);
		farm.setActivity_Dissimilarity(10);
		farm.setIncome_Dissimilarity(0);
		farm.decideActivitySet(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 1);	
	}

	@Test
	public void testUpdateAge() {
		allFarms.get(0).updateAge();
		allFarms.get(1).updateAge();
		
		assertEquals(allFarms.get(0).getAge(), 68);
		assertEquals(allFarms.get(1).getAge(), 68);
	}

	@Test
	public void testUpdateExperienceTwoYears() {
		// test experience array after two years
		allFarms.get(0).updateExperience();
		allFarms.get(0).updateExperience();
		
		Collection<Double[]> year = allFarms.get(0).getExperience().getFarmMap().values();
		List<Double[]> y = new ArrayList<Double[]>(year);
		Double[] verify = {2.5600000000000005, 2.5600000000000005, 1.9200000000000004, 3.2, 3.2, 1.9200000000000004, 1.9200000000000004, 3.2, 3.2, 2.5600000000000005, 2.5600000000000005, 2.5600000000000005, 4.36, 3.2, 1.9200000000000004, 2.5600000000000005, 2.5600000000000005, 2.5600000000000005, 2.5600000000000005, 2.5600000000000005, 1.9200000000000004, 3.2, 2.5600000000000005, 3.2, 2.5600000000000005, 2.5600000000000005, 2.5600000000000005, 1.9200000000000004, 1.9200000000000004, 3.2, 2.5600000000000005, 2.5600000000000005, 2.5600000000000005, 2.5600000000000005, 1.9200000000000004, 1.9200000000000004, 1.9200000000000004, 1.9200000000000004, 2.5600000000000005, 3.2, 2.5600000000000005, 2.5600000000000005, 2.5600000000000005, 1.9200000000000004, 1.9200000000000004, 1.9200000000000004, 1.9200000000000004, 3.2, 2.5600000000000005, 2.5600000000000005, 2.5600000000000005, 3.2, 1.9200000000000004, 2.5600000000000005, 2.5600000000000005, 1.9200000000000004, 3.2, 1.9200000000000004, 2.5600000000000005, 2.5600000000000005, 3.2, 1.9200000000000004, 1.9200000000000004, 2.5600000000000005, 3.2, 2.5600000000000005, 3.2, 2.5600000000000005, 2.5600000000000005, 1.9200000000000004, 1.9200000000000004, 2.5600000000000005};
		Double[] y_list = y.get(2);
		
	 	assertArrayEquals(y_list, verify );
	}
	
	@Test
	public void testSkipUpdateIncomeHistory() {
		Farm farm = allFarms.get(0);
		List<Activity> MP_Activities = new ArrayList<Activity>();	           // list of all farm activities selected by MP model. MP returns list of activities for each farm, thus a list of lists
		MP_Activities = null;
		farm.updateFarmParameters(allFarms, -1, MP_Activities);
		
		Double[] verify = {611.0, 770.0, 488.0, 792.0, 511.0};		   // manually created based on input file
		List<Double> y_list = allFarms.get(0).getIncomeHistory();
		Double[] y = y_list.toArray(new Double[0]);
	 	assertArrayEquals(y, verify);
	}
	
	@Test
	public void testUpdateIncomeHistory() {
		Farm farm = allFarms.get(0);
		List<Activity> MP_Activities = new ArrayList<Activity>();	           // list of all farm activities selected by MP model. MP returns list of activities for each farm, thus a list of lists
		Activity act = new Activity(100,"Activity01");						   // fake updated activity to use during the income udpate
		MP_Activities.add(act);
		
		farm.updateFarmParameters(allFarms, 100, MP_Activities);
		
		Double[] verify = {100.0,611.0, 770.0, 488.0, 792.0};		           // manually created based on input file
		List<Double> y_list = allFarms.get(0).getIncomeHistory();
		Double[] y = y_list.toArray(new Double[0]);
	 	assertArrayEquals(y, verify);
	}
	
	@Test
	public void testUpdateActivityDissimilarityFarm1() {
		Farm farm = allFarms.get(0);
		farm.updateActivityDissimilarity(allFarms);
		double diss = farm.getActivity_Dissimilarity();
		assertEquals(diss, 0.3333333333333333);                                 // hand calculation
	}
	
	@Test
	public void testUpdateActivityDissimilarityFarm2() {
		Farm farm = allFarms.get(1);
		farm.updateActivityDissimilarity(allFarms);
		double unc = farm.getActivity_Dissimilarity();
		assertEquals(unc, 0.5);                                                // hand calculation
	}
	
	@Test
	public void testPopulationIncomeChangePercentBetweenAgents() {	
		// both farm agents should have identical values
		assertEquals(allFarms.get(0).getAveragePopulationIncomeChangeRate(), allFarms.get(1).getAveragePopulationIncomeChangeRate());
	}
	
	@Test
	public void testUpdateAveragePersonalIncomeChangeRate() {	
		Farm farm = allFarms.get(0);
		double rate = farm.getAveragePersonalIncomeChangeRate();
		assertEquals(rate,0.18568767298275496);							       // hand calculated from data file
		
	}
	
	@Test
	public void testIncomeUncertainty() {	
		Farm farm = allFarms.get(0);
		farm.updateIncomeDissimilarity();
		
		double diff = farm.getIncome_Dissimilarity(); 
		
		assertEquals(-0.1762316074687296, diff);							   // excel calculation
	}
	
	@Test
	public void testUpdateSatisfactionFarm1() {
		Farm farm = allFarms.get(0);
		List<Activity> MP_Activities = new ArrayList<Activity>();	           // list of all farm activities selected by MP model. MP returns list of activities for each farm, thus a list of lists
		MP_Activities = null;
		farm.updateFarmParameters(allFarms, -1, MP_Activities);

		double sat = farm.getSatisfaction();
		assertEquals(sat, 24.824767697286585);                                // hand calculation
	} 
	
	@Test
	public void testUpdateSatisfactionFarm2() {
		Farm farm = allFarms.get(3);
		List<Activity> MP_Activities = new ArrayList<Activity>();	           // list of all farm activities selected by MP model. MP returns list of activities for each farm, thus a list of lists
		MP_Activities = null;
		farm.updateFarmParameters(allFarms, -1, MP_Activities);

		double sat = farm.getSatisfaction();
		assertEquals(sat, -21.2035632114007);                                // hand calculation
	} 
	
	public static final String TestDataFile = "./test_data/farm_parameters.csv";
	public static final String TestPreferenceFile = "./test_data/activity_preference.csv";
	public static final String TestInitialActivities = "./test_data/initial_activities.csv";
	public static final String TestInitialIncomes = "./test_data/initial_incomes.csv";
	public static final String TestYearsFile =  "./test_data/performing_years.csv";
	public static final String TestSocialNetworkFile = "./test_data/social_networks.csv";
	
	public static final void useTestData(ReadData reader) {
			reader.FarmParametersFile = TestDataFile;
			reader.ActivityPreferenceFile = TestPreferenceFile;
			reader.InitialActivities = TestInitialActivities;
			reader.InitialIncomes = TestInitialIncomes;
			reader.PerformingYearsFile = TestYearsFile;
			reader.SocialNetworkFile = TestSocialNetworkFile;
	} 

}
