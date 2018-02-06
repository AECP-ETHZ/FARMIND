package agent.farm;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import reader.FarmProductMatrix;
import reader.ReadParameters;

class FarmTest {

	@Test
	void testCreateFarm() {
		Farm farm = new Farm();
		assertEquals(farm, farm);
	}
	
	@Test
	void testMakeDecision() {
		ReadParameters reader = new ReadParameters();										   // read all input data files
		
		Farm farm = new Farm();
		//assertNotEquals(farm, null);
		
		assertEquals(farm.getAge(), 50);
	}

	@Test
	void testUpdateExperiencePlusAge() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateIncomeHistory() {
		fail("Not yet implemented");
	}

}
