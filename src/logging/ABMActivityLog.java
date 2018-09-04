package logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import activity.Activity;

public class ABMActivityLog {

	private String farmId;													   // unique farm id
	private Integer year;													   // which time step this decision was made in
	private int strategy;													   // farm strategy
	private List<String> allActivity;										   // All possible activities in the model
	private List<Activity> currentActivity;									   // current activity of the agent
	private List<String> possibleActivity;								       // set of possible activities by the agent
	private List<Activity> MPSelectedActivity;								   // activity actually selected by the MP
	private int PREVIOUS_ACTIVITY_SET_PRINTING_SIZE;						   // how many activities to print
	private int SELECTED_ACTIVITY_SET_PRINTING_SIZE;						   // how many activities to print

	/** 
	 * Constructor for the CSV Log
	 * @param allActivities:		full set of activities
	 * @param farmId:		    	ID of the farm
	 * @param year:					time period
	 * @param strat:				strategy
	 * @param currentActivity:		current activity(ies) in system
	 * @param MPSelectedActivity:   best activity from the MP model
	 */
	public ABMActivityLog(String modelName, List<String> allActivities, String farmId, Integer year, int strat, List<Activity> currentActivity, List<Activity> MPSelectedActivity) {
		setFarmId(farmId);
		setYear(year);
		setStrategy(strat);
		setCurrentActivity(currentActivity);
		setAllActivity(allActivities);
		setMPSelectedActivity(MPSelectedActivity);
		
		if(modelName.equals("WEEDCONTROL")) {
			SELECTED_ACTIVITY_SET_PRINTING_SIZE = 1;
			PREVIOUS_ACTIVITY_SET_PRINTING_SIZE = 1;
		}
		else {
			SELECTED_ACTIVITY_SET_PRINTING_SIZE = 3;
			PREVIOUS_ACTIVITY_SET_PRINTING_SIZE = 3;
		}
	}
	
	/** 
	 * write output CSV log file based on decision object. This log file can be updated each time period for each agent. 
	 * @param fileName of output file which is previously checked to ensure we will not exceed 1 million lines of data. 
	 */
	public void appendLogFile(String fileName) {
		String PATH = "./output";
		File directory = new File(PATH);
		if(!directory.exists()) {
			directory.mkdir();
		}
		
		File file = new File(String.format("./output/%s_activity.csv", fileName));
		FileWriter fw = null;
		try {
			fw = new FileWriter(file,true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter writer = new PrintWriter(bw);
		
		String name = "year,name,";
		
		for(int i = 0; i < PREVIOUS_ACTIVITY_SET_PRINTING_SIZE; i++) {
			name = name + String.format("previous_activity_%s,",  i+1 );
		}

		for(int i = 0; i < SELECTED_ACTIVITY_SET_PRINTING_SIZE; i++) {
			name = name + String.format("selected_activity_%s,",  i+1 );
		}
		
		name = name + "strategy";
		
		if (file.length() == 0) {
			writer.println(name);
		}
		
		writer.print(String.format("%s,",this.year));
		writer.print(String.format("%s,",this.getFarmId()));
		
		// if PREVIOUS activity set is larger than printing limit, print NA for all options
		if(this.currentActivity.size() == 0 || this.currentActivity.size() > PREVIOUS_ACTIVITY_SET_PRINTING_SIZE) {
			for(int i = 0; i < PREVIOUS_ACTIVITY_SET_PRINTING_SIZE-1 ; i++) {
				writer.print("NA," );
			}
		}
		
		// if PREVIOUS activity set is smaller than printing limit, print those activities plus NA if required
		else {
			for(int i = 0; i < PREVIOUS_ACTIVITY_SET_PRINTING_SIZE; i++) {
				if (this.currentActivity.size() >= (i+1)) {
					writer.print(String.format("%s,",  this.currentActivity.get(i).getName()) );
				}
				else {
					writer.print("NA," );
				}
			}
		}
		
		// if SELECTED activity set is larger than printing limit, print NA for all options
		if(this.MPSelectedActivity.size() == 0 || this.MPSelectedActivity.size() > SELECTED_ACTIVITY_SET_PRINTING_SIZE) {
			for(int i = 0; i < SELECTED_ACTIVITY_SET_PRINTING_SIZE-1 ; i++) {
				writer.print("NA," );
			}
		}
		
		// if SELECTED activity set is smaller than printing limit, print those activities plus NA if required
		else {
			for(int i = 0; i < SELECTED_ACTIVITY_SET_PRINTING_SIZE; i++) {
				if (this.MPSelectedActivity.size() >= (i+1)) {
					writer.print(String.format("%s,",  this.MPSelectedActivity.get(i).getName()) );
				}
				else {
					writer.print("NA," );
				}
			}
		}
		
		writer.print(String.format("%s",this.strategy) );
		
		writer.println("");
		writer.close();
	}
	
	public String getFarmId() {
		return farmId;
	}
	public void setFarmId(String farmId) {
		this.farmId = farmId;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public int getStrategy() {
		return strategy;
	}
	public void setStrategy(int i) {
		this.strategy = i;
	}
	public List<Activity> getCurrentActivity() {
		return currentActivity;
	}
	public void setCurrentActivity(List<Activity> currentActivity) {
		this.currentActivity = currentActivity;
	}
	public List<String> getPossibleActivity() {
		return possibleActivity;
	}
	public void setPossibleActivity(List<String> possibleActivity) {
		this.possibleActivity = possibleActivity;
	}
	public List<String> getAllActivity() {
		return allActivity;
	}
	public void setAllActivity(List<String> allActivity) {
		this.allActivity = allActivity;
	}
	public List<Activity> getMPSelectedActivity() {
		return MPSelectedActivity;
	}
	public void setMPSelectedActivity(List<Activity> mPSelectedActivity) {
		MPSelectedActivity = mPSelectedActivity;
	}
}
