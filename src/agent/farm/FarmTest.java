package agent.farm;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import reader.FarmProductMatrix;

class FarmTest {

	@Test
	void testCreateFarm() {
		Farm farm = new Farm();
		assertNotEquals(farm, null);
	}
	
	@Test
	void testMakeDecision() {
		Farm farm = new Farm();
		assertNotEquals(farm, null);
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
