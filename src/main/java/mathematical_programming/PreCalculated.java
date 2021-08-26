package mathematical_programming;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    
    private Map<String, List<Entry<String, Double>>> yearsHarvest;
	
    /**
     * Constructor
     * 
     * @param cmd : properties
     * @param simYear : MP_Interface conventional argument, unused
     * @param memoryLengthAverage : MP_Interface conventional argument, unused
     */
	public PreCalculated(Properties cmd, int simYear, int memoryLengthAverage) {
	    this.possibleActivitiesState = new HashMap<String,List<String>>();
	    this.yearsHarvest = new HashMap<String,List<Entry<String,Double>>>();
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
	    // TODO : finalize file name and directory structure conventions
	    try (CSVReader reader = new CSVReader(new FileReader(
	        String.format("%s/precalc/Run_%d.csv", cmd.get("data_folder"), year)
	    ), ',')) {
    	    String[] columns = reader.readNext();
    	    
    	    String[] row;
    	    while ((row = reader.readNext()) != null) {
    	        
                String farmName = row[0].trim();
                Map<String,Double> evaluation = new HashMap<String,Double>();
                List<String> possibleActivities = this.possibleActivitiesState.get(farmName);
                for (int i=1; i<columns.length; i++) {
                    if (possibleActivities.contains(columns[i])) {
                        evaluation.put(columns[i], Double.parseDouble(row[i]));
                    }
                }
                
                List<Entry<String, Double>> entryList = new ArrayList<Entry<String, Double>>(evaluation.entrySet());
                Collections.sort(entryList, new Comparator<Entry<String, Double>>(){
                    @Override
                    public int compare(Entry<String, Double> a, Entry<String, Double> b) {
                        double da = a.getValue() == null ? Double.NEGATIVE_INFINITY : a.getValue();
                        double db = b.getValue() == null ? Double.NEGATIVE_INFINITY : b.getValue();
                        return da > db ? -1 : da == db ? 0 : 1;
                    }
                });
                
                this.yearsHarvest.put(farmName, entryList);
            }
	    }
	}

	@Override
	public List<Double> readMPIncomes(Properties cmd, List<Farm> allFarms) throws FileNotFoundException, IOException {
		List<Double> incomes = new ArrayList<Double>();  // list of all farm incomes, same order as allFarms

		for (Farm farm : allFarms) {
		    List<Entry<String, Double>> possibleIncomes = this.yearsHarvest.get(farm.getFarmName());
		    Double income = possibleIncomes.isEmpty() ? 0.0 : possibleIncomes.get(0).getValue();
		    if (possibleIncomes.isEmpty()) {
		        LOGGER.info(String.format("n: %s, a: %s, i: %d",
	                farm.getFarmName(),
	                farm.getCurrentActivity().get(0).getName(),
	                farm.getCurrentActivity().get(0).getID()
	            ));
		    }
		    incomes.add(income);
		}

		return incomes;
	}

	@Override
	public List<ArrayList<Activity>> readMPActivities(Properties cmd, List<Farm> allFarms) throws FileNotFoundException, IOException {
		List<ArrayList<Activity>> activities = new ArrayList<ArrayList<Activity>>();  // list of all farm activities selected by MP model
        
		for (Farm farm : allFarms) {
            List<Entry<String, Double>> topEntry = this.yearsHarvest.get(farm.getFarmName());
            String topActivity = topEntry.isEmpty() ? null : topEntry.get(0).getKey();
            ArrayList<Activity> farmActivities = new ArrayList<Activity>();
            if (topActivity != null) {
                // based on the convention that activity names match this regular expression:
                // /activity\d+/
                // TODO: this convention should be optional. To abandon it, one could store a
                //       dynamically generated map with activity names and ids somewhere.
                int taId = Integer.parseInt(topActivity.substring(8));
                farmActivities.add(new Activity(taId, topActivity));
            }
            activities.add(farmActivities);
        }

		return activities;
	}

	@Override
	public ArrayList<Activity> getExitActivity() {
		// if the agent decides to opt-out we use this to return the correct opt-out activity
		ArrayList<Activity> activities = new ArrayList<Activity>();
		
		Activity exit = new Activity(0,"exit");
		activities.add(exit);
	
		return activities;
	}
}
