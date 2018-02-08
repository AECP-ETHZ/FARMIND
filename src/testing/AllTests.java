package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({	
	  ProductSelectionCalculatorTests.class,
	  FarmTests.class
	})	
public class AllTests {
}
