package testing;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import agent.Farm;
import reader.ReadParameters;

/** 
 * Test farm agent object.
 * @author kellerke
 *
 */
public class FarmTests {
	List<Farm>     allFarms = new ArrayList<Farm>();
	
	@Before 
	public void setup() {
		ReadParameters reader = new ReadParameters();						   // read all input data files
		useTestData(reader);
		allFarms = reader.getFarms(1);						                   // build set of farms with new parameters
		initializeRegionIncomeChangePercent(allFarms);
	}
	
	@Test
	public void testCreateFarm() {
		Farm farm = new Farm();
		assertNotEquals(farm, null);
	}
	
	@Test
	public void testAgeExitDecision() {
		Farm farm = allFarms.get(0);
		farm.getHead().setAge(700);
		farm.makeDecision(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 1);	// exit
	}
	
	@Test
	public void testImitationDecision() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(100);
		farm.setDissimilarity_ISB(100);
		farm.setDissimilarity_Tolerance(0);
		farm.makeDecision(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 2);	
	}
	
	@Test
	public void testRepetionDecision() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(100);
		farm.setDissimilarity_ISB(0);
		farm.setDissimilarity_Tolerance(10);
		farm.makeDecision(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 4);	
	}
	
	@Test
	public void testOptimizationDecision() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(-1);
		farm.setDissimilarity_ISB(0);
		farm.setDissimilarity_Tolerance(10);
		farm.makeDecision(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 3);	
	}
	
	@Test
	public void testOptOutDecision() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(-1);
		farm.setDissimilarity_ISB(10);
		farm.setDissimilarity_Tolerance(0);
		farm.makeDecision(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 1);	
	}

	@Test
	public void testUpdateAge() {
		allFarms.get(0).updateExperiencePlusAge();
		allFarms.get(1).updateExperiencePlusAge();
		
		assertEquals(allFarms.get(0).getAge(), 57);
		assertEquals(allFarms.get(1).getAge(), 55);
	}

	@Test
	public void testUpdateExperience() {
		allFarms.get(0).updateExperiencePlusAge();
		
		Collection<Integer[]> year = allFarms.get(0).getExperience().getProductmap().values();
		List<Integer[]> y = new ArrayList<Integer[]>(year);
		Integer[] verify = {3,5,5,3,5,1};
		Integer[] y_list = y.get(2);
		
	 	assertArrayEquals(y_list, verify );
	}
	
	@Test
	public void testUpdateExperienceTwoYears() {
		// test experience array after two years
		allFarms.get(0).updateExperiencePlusAge();
		allFarms.get(0).updateExperiencePlusAge();
		
		Collection<Integer[]> year = allFarms.get(0).getExperience().getProductmap().values();
		List<Integer[]> y = new ArrayList<Integer[]>(year);
		Integer[] verify = {4, 5, 4, 2, 5, 0};
		Integer[] y_list = y.get(2);
		
	 	assertArrayEquals(y_list, verify );
	}
	
	@Test
	public void testUpdateIncomeHistory() {
		Farm farm = allFarms.get(0);
		farm.updateFarmData(allFarms, 100.00, 0.5);
		
		Double[] verify = {100.00, 63300.0,	52200.0, 48600.0, 56400.0};		   // manually created based on input file
		List<Double> y_list = allFarms.get(0).getIncomeHistory();
		Double[] y = y_list.toArray(new Double[0]);
	 	assertArrayEquals(y, verify);
	}
	
	@Test
	public void testUpdateUncertaintyFarm1() {
		Farm farm = allFarms.get(0);
		farm.updateDissimilarity_ISB(allFarms);
		double unc = farm.getDissimilarity_ISB();
		assertEquals(unc, 0.4);                                                // hand calculation
	}
	
	@Test
	public void testUpdateUncertaintyFarm2() {
		Farm farm = allFarms.get(1);
		farm.updateDissimilarity_ISB(allFarms);
		double unc = farm.getDissimilarity_ISB();
		assertEquals(unc, 0.25);                                                // hand calculation
	}
	
	@Test
	public void testIncomeUncertaintyBetweenClasses() {		
		assertEquals(allFarms.get(0).getRegionIncomeChangePercent(), allFarms.get(1).getRegionIncomeChangePercent());
	}
	
	@Test
	public void testInitialIncomeUncertainty() {
		Farm farm = allFarms.get(0);
		
		farm.updateFarmData(allFarms, -1, 0.5);
		double personalIncomeChangePercent = (farm.getIncomeHistory().get(0) - farm.getLastYearPersonalIncomeAverage()) /farm.getLastYearPersonalIncomeAverage();

		assertEquals(0.17712691771269176, personalIncomeChangePercent);							   // excel calculation
	}
	
	@Test
	public void testUpdatedIncomeUncertainty() {
		Farm farm = allFarms.get(0);
		
		farm.updateFarmData(allFarms, 60000, 0.5);
		double personalIncomeChangePercent = (farm.getIncomeHistory().get(0) - farm.getLastYearPersonalIncomeAverage()) /farm.getLastYearPersonalIncomeAverage();

		assertEquals(0.08843537414965986, personalIncomeChangePercent);							   // excel calculation
		assertEquals(farm.getIncome_ISB(), -0.013503276370462003);
	}
	
	@Test
	public void testUpdatedIncomeUncertaintyTwo() {
		Farm farm = allFarms.get(0);
		
		farm.updateFarmData(allFarms, 30000, 0.5);
		double personalIncomeChangePercent = (farm.getIncomeHistory().get(0) - farm.getLastYearPersonalIncomeAverage()) /farm.getLastYearPersonalIncomeAverage();

		assertEquals(-0.4557823129251701, personalIncomeChangePercent);							   // excel calculation
		assertEquals(farm.getIncome_ISB(), 0.530714410704368);
	}
	
	@Test
	public void testUpdateSatisfactionFarm1() {
		Farm farm = allFarms.get(0);
		farm.updateFarmData(allFarms, -1, 0.5);
		double sat = farm.getSatisfaction();
		assertEquals(sat, 0.38692024958500437);                                // hand calculation
	} 
	
	public static final String TestDataFile = "./test_data/farm_data.csv";
	public static final String TestParameterFile = "./test_data/parameters.csv";
	public static final String TestPreferenceFile = "./test_data/products_preference.csv";
	public static final String TestYearsFile = "./test_data/farming_years.csv";
	public static final String TestSocialNetworkFile = "./test_data/social_networks.csv";
	public static final String TestActivityFile = "./test_data/activities.csv";
	
	private void useTestData(ReadParameters reader) {
			reader.DataFile = TestDataFile;
			reader.ParameterFile = TestParameterFile;
			reader.PreferenceFile = TestPreferenceFile;
			reader.YearsFile = TestYearsFile;
			reader.SocialNetworkFile = TestSocialNetworkFile;
			reader.ActivityFile = TestActivityFile;
	} 
	
	private void initializeRegionIncomeChangePercent(List<Farm> allFarms) {
		double historicalRegionAverage = 0;
		List<Double> initIncome = new ArrayList<Double>();
		double thisYearAverage = 0;
		double percentChange;
		
		for (Farm farm: allFarms) {
			List<Double> income = new ArrayList<Double>(farm.getIncomeHistory());
			initIncome.add(income.get(0));
			income.remove(0);
			historicalRegionAverage = historicalRegionAverage + mean(income);
		}
		historicalRegionAverage = historicalRegionAverage/allFarms.size();
		thisYearAverage = mean(initIncome);
		
		percentChange = (thisYearAverage - historicalRegionAverage) / historicalRegionAverage;
		
		for (Farm farm: allFarms) {
			farm.setRegionIncomeChangePercent(percentChange);
		}
	}
	
	/** 
	 * Return mean value of provided list 
	 * @param list of values to calculate mean with
	 * @return mean
	 */
	private static double mean(List<Double> list) {
		double mean = 0;												       // mean value to return
		
		for (int i = 0; i<list.size(); i++) {
			mean = mean + list.get(i);
		}
		
		return mean / list.size();
	}

}
