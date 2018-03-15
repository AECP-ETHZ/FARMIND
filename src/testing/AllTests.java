package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
/** 
 * Testing suite for all possible unit tests.
 * @author kellerke
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({	
	  DecisionCalculatorTests.class,
	  FarmTests.class,
	  ReadDataTests.class
	})	
public class AllTests {
}
