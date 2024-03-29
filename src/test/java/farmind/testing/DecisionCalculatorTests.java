package farmind.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import farmind.agent.Farm;
import farmind.fuzzy_logic.FuzzyLogicCalculator;
import farmind.main.Consumat;
import farmind.reader.ReadData;

/**
 * This class tests the decision calculator. 
 *
 */
public class DecisionCalculatorTests {
    List<Farm>     allFarms = new ArrayList<Farm>();
    Properties cmd = null;
    
    @Before 
    public void setup() throws FileNotFoundException, IOException {
        String[] args = {"2"};
        this.cmd = Consumat.parseInput(args,true);                             // parse test data control.properties
        ReadData reader = new ReadData(this.cmd);                              // read all input data files
        useTestData(reader);
        this.allFarms = reader.getFarms();                                     // build set of farms with new parameters
    }
    
    @Test
    public void testProductSelectionCalculator() {
        Farm farm = this.allFarms.get(0);
        FuzzyLogicCalculator cal = new FuzzyLogicCalculator(farm, this.allFarms);
        assertNotEquals(cal, null);
    }

    @Test
    public void testLearningVector() {
        Farm farm = this.allFarms.get(0);
        FuzzyLogicCalculator cal = new FuzzyLogicCalculator(farm, this.allFarms);
        double m = farm.getPreferences().getDataElementName().size();          // number of products in system

        List<Double> Q = new ArrayList<Double>();                              // learning by doing vector for specific farm
        double time = 0;                                                       // years of experience
        double k = farm.getLearningRate();                                     // scale factor
        double q;                                                              // calculated score
        
        for (int i = 0; i < m; i++) {
            time = farm.getExperience().getFarmDataElementValue(farm.getFarmName(), farm.getPreferences().getDataElementName().get(i) );
            q = 1 / ( 1 +  Math.exp( (-k*time) ));
            Q.add(q);
        }
        Q = normalizeList(Q);
                
        List<Double> L = cal.L;
        int l = L.size();
        L.remove(l-1);
        L.remove(l-2);                                                         // original L vector has q+,q- at end
        assertEquals(L,Q);
    }
    
    @Test
    public void testPreferenceVector() {
        Farm farm = this.allFarms.get(0);
        FuzzyLogicCalculator cal = new FuzzyLogicCalculator(farm, this.allFarms);
        double m = farm.getPreferences().getDataElementName().size();          // number of products in system
        List<Double> P = new ArrayList<Double>();                              // rank of all product preferences for specific farm
        Double[] R;                                                            // Product preference vector 

        R = farm.getPreferences().getFarmMap().get(farm.getFarmName());
        
        for (int i = 0; i< m; i++) {
            P.add(R[i]); //  1 - R[i]/m);
        }
        
        List<Double> Px = cal.P;
        int l = Px.size();
        Px.remove(l-1);
        Px.remove(l-2); 
        
        assertEquals(Px,normalizeList(P));
    }

    @Test
    public void testNDSelection() {
        Farm farm = this.allFarms.get(0);
        FuzzyLogicCalculator cal = new FuzzyLogicCalculator(farm, this.allFarms);
        List<Double> crit1 = Arrays.asList(15.0,10.0,5.0,1.0,5.0);             // sample from document
        List<Double> crit2 = Arrays.asList(6.0,14.0,10.0,1.0,5.0);
        List<Double> crit3 = Arrays.asList(10.0,7.0,13.0,1.0,5.0);
        cal.L = crit1;
        cal.P = crit2;
        cal.S = crit3;
        cal.getImitationActivities();
        
        List<Double> verify = Arrays.asList(1.0, 0.59, 0.2657142857142857  );  // Arrays.asList(1.0, 0.7466666666666666, 0.566); 
        List<Double> x = cal.ND;
        assertEquals(x,verify);
    }
    
    public static final String TestDataFile = "./test_data/farm_parameters.csv";
    public static final String TestPreferenceFile = "./test_data/activity_preference.csv";
    public static final String TestInitialActivities = "./test_data/initial_activities.csv";
    public static final String TestInitialIncomes = "./test_data/initial_incomes.csv";
    public static final String TestYearsFile =  "./test_data/performing_years.csv";
    public static final String TestSocialNetworkFile = "./test_data/social_networks.csv";
    
    public static final void useTestData(ReadData reader) {
            reader.FarmParametersFile = TestDataFile;
            reader.ActivityPreferenceFile = TestPreferenceFile;
            reader.InitialActivities = TestInitialActivities;
            reader.InitialIncomes = TestInitialIncomes;
            reader.InitialPerformingYears = TestYearsFile;
            reader.SocialNetworkFile = TestSocialNetworkFile;
    } 
    
    /**
     * Create a normalized list from 0 to 1
     * @param list unormalized list
     * @return normalized list
     */
    private static List<Double> normalizeList(List<Double> list) {
        List<Double> normalizedList = new ArrayList<Double>();                 // normalized list to return

        double min = min(list);
        double max = max(list);
        
        for (int i = 0; i<list.size();i++) {
             normalizedList.add( (list.get(i) - min) / (max - min) );
        }
        return normalizedList;
    }
    
    /** 
     * Find minimum of List of doubles
     * @param list of input values
     * @return minimum value
     */
    private static double min(List<Double> list) {
        double min = 1;
        double temp = 0;
        
        for(int i=0; i<list.size();i++) {
            temp = list.get(i);
            if (temp < min) { min = temp;}
        }
        return min;
    }
    
    /** 
     * Find max of List of doubles
     * @param list of input values
     * @return max value
     */
    private static double max(List<Double> list) {
        double max = 0;
        double temp = 0;
        
        for(int i=0; i<list.size();i++) {
            temp = list.get(i);
            if (temp > max) { max = temp;}
        }
        return max;
    }
}
