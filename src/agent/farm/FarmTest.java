package agent.farm;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reader.ReadParameters;

class FarmTest {
	List<Farm>     allFarms = new ArrayList<Farm>();
	
	@BeforeEach 
	void setup() {
		ReadParameters reader = new ReadParameters();						   // read all input data files
		useTestData(reader);
		allFarms = reader.getFarms(1);						                   // build set of farms with new parameters
		initializeRegionIncomeChangePercent(allFarms);
	}
	
	@Test
	void testCreateFarm() {
		Farm farm = new Farm();
		assertNotEquals(farm, null);
	}
	
	@Test
	void testAgeExitDecision() {
		Farm farm = allFarms.get(0);
		farm.getHead().setAge(700);
		farm.makeDecision(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 1);	// exit
	}
	
	@Test
	void testImitationDecision() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(100);
		farm.setUncertainty(100);
		farm.setTolerance(0);
		farm.makeDecision(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 2);	
	}
	
	@Test
	void testRepetionDecision() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(100);
		farm.setUncertainty(0);
		farm.setTolerance(10);
		farm.makeDecision(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 4);	
	}
	
	@Test
	void testOptimizationDecision() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(-1);
		farm.setUncertainty(0);
		farm.setTolerance(10);
		farm.makeDecision(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 3);	
	}
	
	@Test
	void testOptOutDecision() {
		Farm farm = allFarms.get(0);
		
		farm.setSatisfaction(-1);
		farm.setUncertainty(10);
		farm.setTolerance(0);
		farm.makeDecision(allFarms);
		
		int strat = farm.getStrategy();
		assertEquals(strat, 1);	
	}

	@Test
	void testUpdateAge() {
		allFarms.get(0).updateExperiencePlusAge();
		allFarms.get(1).updateExperiencePlusAge();
		
		assertEquals(allFarms.get(0).getAge(), 57);
		assertEquals(allFarms.get(1).getAge(), 55);
	}

	@Test
	void testUpdateExperience() {
		allFarms.get(0).updateExperiencePlusAge();
		
		Collection<Integer[]> year = allFarms.get(0).getExperience().getProductmap().values();
		List<Integer[]> y = new ArrayList<Integer[]>(year);
		Integer[] verify = {3,4,5,3,5,1};
		Integer[] y_list = y.get(2);
		
	 	assertArrayEquals(y_list, verify );
	}
	
	@Test
	void testUpdateExperienceTwoYears() {
		// test experience array after two years
		allFarms.get(0).updateExperiencePlusAge();
		allFarms.get(0).updateExperiencePlusAge();
		
		Collection<Integer[]> year = allFarms.get(0).getExperience().getProductmap().values();
		List<Integer[]> y = new ArrayList<Integer[]>(year);
		Integer[] verify = {4, 3, 4, 2, 5, 0};
		Integer[] y_list = y.get(2);
		
	 	assertArrayEquals(y_list, verify );
	}
	
	@Test
	void testUpdateIncomeHistory() {
		Farm farm = allFarms.get(0);
		farm.updateFarmData(allFarms, 100.00, 0.5);
		
		Double[] verify = {100.00, 63300.0,	52200.0, 48600.0, 56400.0};		   // manually created based on input file
		List<Double> y_list = allFarms.get(0).getIncomeHistory();
		Double[] y = y_list.toArray(new Double[0]);
	 	assertArrayEquals(y, verify);
	}
	
	@Test
	void testUpdateUncertaintyFarm1() {
		Farm farm = allFarms.get(0);
		farm.updateUncertainty(allFarms);
		double unc = farm.getUncertainty();
		assertEquals(unc, 0.4);                                                // hand calculation
	}
	
	@Test
	void testUpdateUncertaintyFarm2() {
		Farm farm = allFarms.get(1);
		farm.updateUncertainty(allFarms);
		double unc = farm.getUncertainty();
		assertEquals(unc, 0.25);                                                // hand calculation
	}
	
	@Test
	void testIncomeUncertaintyBetweenClasses() {		
		assertEquals(allFarms.get(0).getRegionIncomeChangePercent(), allFarms.get(1).getRegionIncomeChangePercent());
	}
	
	@Test
	void testInitialIncomeUncertainty() {
		Farm farm = allFarms.get(0);
		
		farm.updateFarmData(allFarms, -1, 0.5);
		double personalIncomeChangePercent = (farm.getIncomeHistory().get(0) - farm.getLastYearPersonalIncomeAverage()) /farm.getLastYearPersonalIncomeAverage();

		assertEquals(0.17712691771269176, personalIncomeChangePercent);							   // excel calculation
	}
	
	@Test
	void testUpdatedIncomeUncertainty() {
		Farm farm = allFarms.get(0);
		
		farm.updateFarmData(allFarms, 60000, 0.5);
		double personalIncomeChangePercent = (farm.getIncomeHistory().get(0) - farm.getLastYearPersonalIncomeAverage()) /farm.getLastYearPersonalIncomeAverage();

		assertEquals(0.08843537414965986, personalIncomeChangePercent);							   // excel calculation
		assertEquals(farm.getIncomeUncertainty(), 0.0);
	}
	
	@Test
	void testUpdatedIncomeUncertaintyTwo() {
		Farm farm = allFarms.get(0);
		
		farm.updateFarmData(allFarms, 30000, 0.5);
		double personalIncomeChangePercent = (farm.getIncomeHistory().get(0) - farm.getLastYearPersonalIncomeAverage()) /farm.getLastYearPersonalIncomeAverage();

		assertEquals(-0.4557823129251701, personalIncomeChangePercent);							   // excel calculation
		assertEquals(farm.getIncomeUncertainty(), 1.0);
	}
	
	@Test
	void testUpdateSatisfactionFarm1() {
		Farm farm = allFarms.get(0);
		farm.updateFarmData(allFarms, -1, 0.5);
		double sat = farm.getSatisfaction();
		assertEquals(sat, 0.38692024958500437);                                // hand calculation
	} 
	
	// satisfaction tests
	
	public static final String TestDataFile = "./test_data/farm_data.csv";
	public static final String TestParameterFile = "./test_data/parameters.csv";
	public static final String TestPreferenceFile = "./test_data/products_preference.csv";
	public static final String TestYearsFile = "./test_data/farming_years.csv";
	public static final String TestSocialNetworkFile = "./test_data/social_networks.csv";
	public static final String TestCropFile = "./test_data/crop_classification.csv";
	public static final String TestLivestockFile = "./test_data/livestock_classification.csv";
	
	private void useTestData(ReadParameters reader) {
			reader.DataFile = TestDataFile;
			reader.ParameterFile = TestParameterFile;
			reader.PreferenceFile = TestPreferenceFile;
			reader.YearsFile = TestYearsFile;
			reader.SocialNetworkFile = TestSocialNetworkFile;
			reader.CropFile = TestCropFile;
			reader.LivestockFile = TestLivestockFile;
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
