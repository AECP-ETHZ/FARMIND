package testing;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(JUnitPlatform.class)
@SuiteClasses({FarmTests.class, ProductSelectionCalculatorTests.class})
public class AllTests {
	

}
