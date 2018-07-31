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

	
	/** 
	 * Constructor for the CSV Log
	 * @param allActivities:		full set of activities
	 * @param farmId:		    	ID of the farm
	 * @param possibleActivities:	full activity set
	 * @param year:					time period
	 * @param strat:				strategy
	 * @param currentActivity:		current activity(ies) in system
	 * @param income:				income of farm
	 * @param learning_rate: 		learning rate for the agent
	 * @param farm: 				specific farm for this decision object
	 */
	public ABMActivityLog(List<String> allActivities, String farmId, Integer year, int strat, List<Activity> currentActivity, List<Activity> MPSelectedActivity) {
		setFarmId(farmId);
		setYear(year);
		setStrategy(strat);
		setCurrentActivity(currentActivity);
		setAllActivity(allActivities);
		setMPSelectedActivity(MPSelectedActivity);
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
		
		if (file.length() == 0) {
			writer.println("year,name,previous_activity,selected_activity,strategy");
		}
		
		writer.print(String.format("%s,",this.year));
		writer.print(String.format("%s,",this.getFarmId()));
		
		
		for(int i = 0; i < this.currentActivity.size(); i++) {
			writer.print(String.format("%s,",  this.currentActivity.get(i).getName()) );
		}
		
		for(int i = 0; i < this.MPSelectedActivity.size(); i++) {
			writer.print(String.format("%s,",  this.MPSelectedActivity.get(i).getName()) );
		}
		
		writer.print(String.format("%s,",this.strategy) );
		
		
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
