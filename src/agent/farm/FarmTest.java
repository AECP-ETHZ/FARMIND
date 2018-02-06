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
	}
	
	@Test
	void testCreateFarm() {
		Farm farm = new Farm();
		assertNotEquals(farm, null);
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
		allFarms.get(0).updateIncomeHistory(100);
		
		Double[] verify = {100.00, 63300.0,	52200.0, 48600.0, 56400.0};
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

}
