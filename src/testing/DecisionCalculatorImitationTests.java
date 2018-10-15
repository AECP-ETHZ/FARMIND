package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import agent.Farm;
import fuzzy_logic.FuzzyLogicCalculator;
import reader.ReadData;		

/**
 * This class tests the decision calculator. 
 *
 */
public class DecisionCalculatorImitationTests {
	List<Farm>     allFarms = new ArrayList<Farm>();
	
	@Before 
	public void setup() {
		ReadData reader = new ReadData();						               // read all input data files
		useTestData(reader);
		allFarms = reader.getFarms();						                   // build set of farms with new parameters
	}
	
	@Test
	public void testProductSelectionCalculator() {
		Farm farm = allFarms.get(0);
		FuzzyLogicCalculator cal = new FuzzyLogicCalculator(farm, allFarms);
		assertNotEquals(cal, null);
	}
	
	@Test
	// beta l and beta p set to 0 with beta_s active
	public void testbeta_s_farm0() {
		Farm farm = allFarms.get(0);
		FuzzyLogicCalculator cal = new FuzzyLogicCalculator(farm, allFarms);
		
		List<String> verify = Arrays.asList("activity01", "activity02", "activity03", "activity04", "activity05", "activity06"); 
		List<String> x = cal.getImitationActivities();
		assertEquals(x,verify);
	}
	
	@Test
	// beta l and beta p set to 0 with beta_s active
	public void testbeta_s_farm2() {
		Farm farm = allFarms.get(2);
		FuzzyLogicCalculator cal = new FuzzyLogicCalculator(farm, allFarms);
		
		List<String> verify = Arrays.asList("activity06", "activity07", "activity08", "activity09", "activity10", "activity01"); 
		List<String> x = cal.getImitationActivities();
		assertEquals(x,verify);
	}
	
	public static final String TestDataFile = "./test_data/decision_files/farm_parameters.csv";
	public static final String TestPreferenceFile = "./test_data/decision_files/activity_preference.csv";
	public static final String TestInitialActivities = "./test_data/decision_files/initial_activities.csv";
	public static final String TestInitialIncomes = "./test_data/decision_files/initial_incomes.csv";
	public static final String TestYearsFile =  "./test_data/decision_files/performing_years.csv";
	public static final String TestSocialNetworkFile = "./test_data/decision_files/social_networks.csv";
	
	public static final void useTestData(ReadData reader) {
			reader.FarmParametersFile = TestDataFile;
			reader.ActivityPreferenceFile = TestPreferenceFile;
			reader.InitialActivities = TestInitialActivities;
			reader.InitialIncomes = TestInitialIncomes;
			reader.InitialPerformingYears = TestYearsFile;
			reader.SocialNetworkFile = TestSocialNetworkFile;
	} 
}