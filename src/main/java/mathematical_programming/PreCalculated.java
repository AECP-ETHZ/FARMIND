package mathematical_programming;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
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
	
    private Map<String,Set<String>> possibleActivitiesState;
    
    private Map<String, List<Entry<String, Score>>> yearsHarvest;
    
    private Map<String, List<String>> incomeActivityMap;
    
    private final static String MP_DATA_FOLDER = "MPdata";
	
    /**
     * Constructor
     * 
     * @param cmd : properties
     * @param simYear : MP_Interface conventional argument, unused
     * @param memoryLengthAverage : MP_Interface conventional argument, unused
     * @throws IOException 
     * @throws FileNotFoundException 
     */
	public PreCalculated(Properties cmd, int simYear, int memoryLengthAverage) throws FileNotFoundException, IOException {
	    this.possibleActivitiesState = new HashMap<String,Set<String>>();
	    this.yearsHarvest = new HashMap<String,List<Entry<String,Score>>>();
	    
	    this.incomeActivityMap = new HashMap<String,List<String>>();
	    try (CSVReader reader = new CSVReader(new FileReader(
	            String.format("%s/%s/transformation-activity-income.csv", cmd.get("data_folder"), PreCalculated.MP_DATA_FOLDER)
	        ), ',')) {
            String[] columns = reader.readNext();
            
            String[] row;
            while ((row = reader.readNext()) != null) {
                
                String income = row[0].trim();
                List<String> activities = new ArrayList<String>();
                for (int i=1; i<columns.length; i++) {
                    if (row[i].trim() == "1") {
                        activities.add(columns[i].trim());
                    }
                }
                
                this.incomeActivityMap.put(income, activities);
            }
        }
	}
	
    @Override
    public void inputsforMP(Farm farm, List<String> possibleActivity) throws IOException {
        this.possibleActivitiesState.put(farm.getFarmName(), new HashSet<String>());
        this.possibleActivitiesState.get(farm.getFarmName()).addAll(possibleActivity);
    }

	@Override
	public void runModel(Properties cmd, int nFarms, int year, boolean pricingAverage, int memoryLengthAverage) throws IOException {
	    LOGGER.info("run calculation for year "+year
                + "\n      nFarms: "+nFarms
                + "\n      pricingAverage: "+pricingAverage
                + "\n      memoryLengthAverage: "+memoryLengthAverage);
	    try (CSVReader reader = new CSVReader(new FileReader(
	        String.format("%s/MPdata/run%d.csv", cmd.get("data_folder"), PreCalculated.MP_DATA_FOLDER, year)
	    ), ',')) {
	        String[] incomes = reader.readNext();
            Map<String,Map<String,Score>> allFarmScores = new HashMap<String,Map<String,Score>>();
            
            // incomes
    	    Map<String,Map<String,Double>> farmIncomes = new HashMap<String,Map<String,Double>>();
            String[] row;
    	    while ((row = reader.readNext()) != null) {
    	        
                String farmName = row[0].trim();
                if (farmName == "") break;
                
                Map<String,Double> farmIncome = new HashMap<String,Double>();
                for (int i=1; i<incomes.length; i++) {
                    farmIncome.put(incomes[i], Double.parseDouble(row[i]));
                }
                farmIncomes.put(farmName, farmIncome);
    	    }
    	    
    	    @SuppressWarnings("unused")
            String[] climateLoad = reader.readNext(); // unnecessary: the row with ",THG1,THG2,..."
    	        // is just there to separate incomes from climate effect
    	    
            // impact
            while ((row = reader.readNext()) != null) {
                
                String farmName = row[0].trim();
                if (farmName == "") break;
                
                Map<String,Score> farmScore = new HashMap<String,Score>();
                for (int i=1; i<incomes.length; i++) {
                    if (this.acceptableIncome(farmName, incomes[i])) {
                    farmScore.put(incomes[i], new Score(
                        farmIncomes.get(farmName).get(incomes[i]),
                        Double.parseDouble(row[i])
                    ));
                    }
                }
                
                // now we now everything about that farm
                allFarmScores.put(farmName, farmScore);
            }
            
            for (Entry<String, Map<String, Score>> farmScores : allFarmScores.entrySet()) {
                List<Entry<String, Score>> entryList = new ArrayList<Entry<String, Score>>(farmScores.getValue().entrySet());
                Collections.sort(entryList, new Comparator<Entry<String, Score>>(){
                    @Override
                    public int compare(Entry<String, Score> a, Entry<String, Score> b) {
                        Score sa = a.getValue();
                        Score sb = b.getValue();
                        
                        if (sa.income > sb.income) return -1;
                        if (sa.income < sb.income) return 1;
                        
                        if (sa.impact < sb.impact) return -1;
                        if (sa.impact > sb.impact) return 1;
                        
                        return 0;
                   }
                });
                
                this.yearsHarvest.put(farmScores.getKey(), entryList);
            }
	    }
	}

	private boolean acceptableIncome(String farmName, String income) {
	    Set<String> possibleActivities = this.possibleActivitiesState.get(farmName);
	    List<String> incomeActivities = this.incomeActivityMap.get(income);
	    for (String activity : incomeActivities) {
	        if (!possibleActivities.contains(activity)) return false;
	    }
	    return true;
	}
	
	@Override
	public List<Double> readMPIncomes(Properties cmd, List<Farm> allFarms) throws FileNotFoundException, IOException {
		List<Double> incomes = new ArrayList<Double>();  // list of all farm incomes, same order as allFarms

		for (Farm farm : allFarms) {
		    List<Entry<String, Score>> possibleScores = this.yearsHarvest.get(farm.getFarmName());
		    Double income = possibleScores.isEmpty() ? 0.0 : possibleScores.get(0).getValue().income;
		    if (possibleScores.isEmpty()) {
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
            ArrayList<Activity> farmActivities = new ArrayList<Activity>();
            
            // that's not really a for loop, it just processes the first item - if any - and breaks.
            // (another way to say: "if the list is not empty, get its first item and do something with it")
            for (Entry<String, Score> topEntry : this.yearsHarvest.get(farm.getFarmName())) {
            
                String topScoreIncome = topEntry.getKey();
                
                for (String farmActivity : this.incomeActivityMap.get(topScoreIncome)) {
                    // based on the convention that activity names match this regular expression:
                    // /activity\d+/
                    // TODO: this convention should be foramalized or optional. To abandon it, one could store a
                    //       dynamically generated map with activity names and ids somewhere.
                    farmActivities.add(new Activity(Integer.parseInt(farmActivity.substring(8)), farmActivity));
                }
                
                // as stated above - not really a for loop.
                break;
            }
            activities.add(farmActivities);
        }

		return activities;
	}

	@Override
	public ArrayList<Activity> getExitActivity() {
		// if the agent decides to opt-out we use this to return the correct opt-out activity
		ArrayList<Activity> activities = new ArrayList<Activity>();
		
		Activity exit = new Activity(1,"activity01");
		activities.add(exit);
	
		return activities;
	}
}
