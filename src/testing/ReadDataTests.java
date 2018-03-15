package testing;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import activity.Activity;
import agent.Farm;
import reader.ReadData;
import testing.FarmTests;

public class ReadDataTests {
	ReadData reader = new ReadData();						                   // read all input data files
	List<Object> ret = new ArrayList<Object>();							       // object to return
	
	@Before 
	public void setup() {
		copyResultsFile();
		ret = reader.readIncomeResults();
	} 
	
	@SuppressWarnings("unchecked")
	@Test
	// if there is a mismatch in the strategy counting this will fail
	public void TestStrategyCount() {
		List<Activity> strat = new ArrayList<Activity>();					   // list of selected strategies for each agent (one per agent)
		List<Double> incomes = new ArrayList<Double>();						   // list of incomes from result file

		strat = (List<Activity>) ret.get(1);
		incomes = (List<Double>) ret.get(0);
		assertEquals(strat.size(),incomes.size());
	}
	
	@Test
	public void TestReadingFarms() {
		ReadData reader = new ReadData();						   // read all input data files
		FarmTests.useTestData(reader);
		List<Farm> allFarms = reader.getFarms(1);			      // build set of farms with new parameters
		assertNotEquals(allFarms,null);
	}
	
	
	// read in empty file
	// read in incomplete strategy list
	
	public void copyResultsFile() {
		Runtime runtime = Runtime.getRuntime();								   // java runtime to run commands
		String name = System.getProperty("os.name").toLowerCase();;
		try {
			if (name.startsWith("win") ){
				runtime.exec("cmd /C" + "run_gams.bat");
			}
			if (name.startsWith("mac")) {
				runtime.exec("/bin/bash -c ./run_gams_mac.command");				   // actually run command
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}						   
	}
	

}
