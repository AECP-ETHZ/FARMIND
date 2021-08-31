package logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import activity.Activity;
import agent.Farm;

/** 
 * ABM activity log which writes to output csv file.
 * 
 * @author kellerke
 */
public class ABMActivityLog {

	private final String farmId;                           // unique farm id
	private final Integer year;                            // which time step this decision was made in
	private final int strategy;                            // All possible activities in the model
	private final List<Activity> currentActivity;          // current activity of the agent
	private final List<Activity> MPSelectedActivity;       // activity actually selected by the MP
	private final int PREVIOUS_ACTIVITY_SET_PRINTING_SIZE; // how many activities to print
	private final int SELECTED_ACTIVITY_SET_PRINTING_SIZE; // how many activities to print
	private final double income;                           // income of farm
	
	/** 
	 * Constructor for the CSV Log
     * @param modelName ::          string name of agent
	 * @param year ::			    time period
	 * @param MPSelectedActivity :: best activity from the MP model
	 * @param farm ::			    specific farm for this decision object
	 */
	public ABMActivityLog(String modelName, Integer year, final List<Activity> MPSelectedActivity, Double MP_Incomes, final Farm farm) {
		this.farmId = farm.getFarmName();
		this.year = year;
		this.strategy = farm.getStrategy();
		this.currentActivity = farm.getCurrentActivity();
		this.MPSelectedActivity = MPSelectedActivity;
		this.income = MP_Incomes;
		
		switch (modelName) {
    		case "WEEDCONTROL": 
                this.SELECTED_ACTIVITY_SET_PRINTING_SIZE = 1;
                this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE = 1;
                break;
    		case "PRECALCULATED":
                this.SELECTED_ACTIVITY_SET_PRINTING_SIZE = 5;
                this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE = 5;
                break;
    		default:
                this.SELECTED_ACTIVITY_SET_PRINTING_SIZE = 3;
                this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE = 3;
                break;
		}
	}
	
	/** 
	 * write output CSV log file based on decision object. This log file can be updated each time period for each agent. 
	 * Does a number of checks to see what size of information is being printed to ensure we do not write more than is useful.
	 * @param fileName ::output file which is previously checked to ensure we will not exceed 1 million lines of data. 
	 * @param averagePrice :: boolean to indicate which log file to write to
	 * @throws IOException 
	 */
	public void appendLogFile(String fileName, boolean averagePrice) throws IOException {
		String PATH = "./output";
		File directory = new File(PATH);
		if(!directory.exists()) {
			directory.mkdir();
		}

		File file = new File(String.format("%s/%s_activity_%sPrice.csv",
		        PATH, fileName, averagePrice ? "average" : "actual"));
		boolean appending = file.exists() && file.length() > 0;
		try (PrintWriter writer = new PrintWriter(
		    new BufferedWriter(new FileWriter(file, true))
		)) {
		
    		String name = "year,name,";
    		
    		for(int i = 0; i < this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE; i++) {
    			name = name + String.format("previous_activity_%c,", (char)('a' + i));
    		}
    
    		for(int i = 0; i < this.SELECTED_ACTIVITY_SET_PRINTING_SIZE; i++) {
    			name = name + String.format("selected_activity_%c,", (char)('a' + i));
    		}
    		
    		name = name + "strategy";
    		name = name + ",income";
    		
    		if (!appending) {
    			writer.println(name);
    		}
    		
    		writer.print(String.format("%s,",this.year));
    		writer.print(String.format("%s,",this.farmId));
    		
    		// if PREVIOUS activity set is larger than printing limit, print NA for all options
    		if(this.currentActivity.size() == 0 || this.currentActivity.size() > this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE) {
    			for(int i = 0; i < this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE-1 ; i++) {
    				writer.print("NA," );
    			}
    		}
    		
    		// if PREVIOUS activity set is smaller than printing limit, print those activities plus NA if required
    		else {
    			for(int i = 0; i < this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE; i++) {
    				if (this.currentActivity.size() >= (i+1)) {
    					writer.print(String.format("%s,",  this.currentActivity.get(i).getName()) );
    				}
    				else {
    					writer.print("NA," );
    				}
    			}
    		}
    		
    		// if SELECTED activity set is larger than printing limit, print NA for all options
    		if(this.MPSelectedActivity.size() == 0 || this.MPSelectedActivity.size() > this.SELECTED_ACTIVITY_SET_PRINTING_SIZE) {
    			for(int i = 0; i < this.SELECTED_ACTIVITY_SET_PRINTING_SIZE-1 ; i++) {
    				writer.print("NA," );
    			}
    		}
    		
    		// if SELECTED activity set is smaller than printing limit, print those activities plus NA if required
    		else {
    			for(int i = 0; i < this.SELECTED_ACTIVITY_SET_PRINTING_SIZE; i++) {
    				if (this.MPSelectedActivity.size() >= (i+1)) {
    					writer.print(String.format("%s,",  this.MPSelectedActivity.get(i).getName()) );
    				}
    				else {
    					writer.print("NA," );
    				}
    			}
    		}
    		
    		writer.print(String.format("%s,",this.strategy) );
    		writer.print(String.format("%s",this.income) );
    		
    		writer.println("");
		}
	}
}
