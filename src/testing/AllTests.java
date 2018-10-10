package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
/** 
 * This class tests suite for all possible unit tests
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({	
	  DecisionCalculatorTests.class,
	  FarmTests.class,
	  DecisionCalculatorImitationTests.class,
	})	
public class AllTests {
}
