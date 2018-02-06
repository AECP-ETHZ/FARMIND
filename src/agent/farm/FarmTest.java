package agent.farm;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import reader.FarmProductMatrix;
import reader.ReadParameters;

class FarmTest {

	@Test
	void testCreateFarm() {
		Farm farm = new Farm();
		assertNotEquals(farm, null);
	}
	
	@Test
	void testMakeDecision() {
		ReadParameters reader = new ReadParameters();						   // read all input data files
		useTestData(reader);
		List<Farm>     allFarms = reader.getFarms(1);						   // build set of farms with new parameters
		assertNotEquals(allFarms, null);
	}

	@Test
	void testUpdateExperiencePlusAge() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateIncomeHistory() {
		fail("Not yet implemented");
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
