package mathematical_programming;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import com.opencsv.CSVReader;

import activity.Activity;
import agent.Farm;

/** 
 * In this Model the results are not calculated from scratch but read from CSV files containing pre-calculated results. 
 * 
 * @author schmide
 *
 */

public class PreCalculated implements MP_Interface{
	private static final Logger LOGGER = Logger.getLogger("FARMIND_LOGGING");
	
    private Map<String,List<String>> possibleActivitiesState;
    
    private Map<String,Map<String,Double>> yearsHarvest;
	
	public PreCalculated(Properties cmd, int simYear, int memoryLengthAverage) {
	    this.possibleActivitiesState = new HashMap<String,List<String>>();
	    this.yearsHarvest = new HashMap<String,Map<String,Double>>();
	}
	
    @Override
    public void inputsforMP(Farm farm, List<String> possibleActivity) throws IOException {
        this.possibleActivitiesState.put(farm.getFarmName(), new ArrayList<String>());
        this.possibleActivitiesState.get(farm.getFarmName()).addAll(possibleActivity);
    }

	@Override
	public void runModel(Properties cmd, int nFarms, int year, boolean pricingAverage, int memoryLengthAverage) throws IOException {
	    LOGGER.info("run calculation for year "+year
                + "\n      nFarms: "+nFarms
                + "\n      pricingAverage: "+pricingAverage
                + "\n      memoryLengthAverage: "+memoryLengthAverage);

	    try (CSVReader reader = new CSVReader(new FileReader(
	        String.format("%s/Run_%d.csv", cmd.get("precalc_data_dir"), year)
	    ), ',')) {
    	    String[] columns = reader.readNext();
    	    
    	    String[] row;
    	    while ((row = reader.readNext()) != null) {
    	        
                String farmName = row[0].strip();
                this.yearsHarvest.put(farmName, new HashMap<String,Double>());
                
                for (int i=1; i<columns.length; i++) {
                    this.yearsHarvest.get(farmName)
                                     .put(columns[i],
                                          Double.parseDouble(row[i]));
                }
            }
	    }
	}

	@Override
	public List<Double> readMPIncomes(Properties cmd, List<Farm> allFarms) throws FileNotFoundException, IOException {
		List<Double> incomes = new ArrayList<Double>();						   // list of all farm incomes   

		for (Farm farm : allFarms) {
		    Double income = this.yearsHarvest
                .get(farm.getFarmName())
                .get(farm.getCurrentActivity().get(0).getName());
		    incomes.add(income == null ? 0 : income);
		    if (income == null) {
		        this.LOGGER.info(
		                String.format("n: %s, a: %s, i: %d",
	                farm.getFarmName(),
	                farm.getCurrentActivity().get(0).getName(),
	                farm.getCurrentActivity().get(0).getID()
	            ));
		    }
		}

		return incomes;
	}

	@Override
	public List<ArrayList<Activity>> readMPActivities(Properties cmd, List<Farm> allFarms) throws FileNotFoundException, IOException {
		List<ArrayList<Activity>> activities = new ArrayList<ArrayList<Activity>>();	   	 	   // list of all farm activities selected by MP model
        
		for (Farm farm : allFarms) {
            double maxD = Double.NEGATIVE_INFINITY;
            String topActivity = null;
            for (Entry<String, Double> e : this.yearsHarvest.get(farm.getFarmName()).entrySet()) {
                double d = e.getValue();
                if (d > maxD) {
                    maxD = d;
                    topActivity = e.getKey();
                }
                if (d == maxD) {
                    //TODO: ???
                    topActivity = e.getKey();
                }
                
            }
            
            ArrayList<Activity> farmActivities = new ArrayList<Activity>();
            //if (topActivity != null) {
                farmActivities.add(new Activity(Integer.parseInt(topActivity.substring(8)), topActivity));
            //}
            activities.add(farmActivities);
        }

		return activities;
	}

	@Override
	public ArrayList<Activity> getExitActivity() {
		// if the agent decides to opt-out we use this to return the correct opt-out activity
		ArrayList<Activity> activities = new ArrayList<Activity>();	   	 	       // list of all farm activities selected by MP model
		Activity exit = new Activity(0,"exit");
		activities.add(exit);
	
		return activities;
	}
}
