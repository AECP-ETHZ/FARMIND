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
 * Logs the output of each step of the ABM process to an output csv log of all decisions and parameters during the model execution. 
 * @author kellerke
 *
 */
public class ABMTimeStepLog {

	private String farmId;													   // unique farm id
	private Integer year;													   // which time step this decision was made in
	private int strategy;													   // farm strategy
	private double income;													   // income of time step
	private double learning_rate;											   // learning rate of agent
	private List<String> allActivity;										   // All possible activities in the model
	private List<Activity> currentActivity;									   // current activity of the agent
	private List<String> possibleActivity;								       // set of possible activities by the agent
	private Farm farm;														   // farm holds parameters
	private double activity_diss;
	private double income_diss;
	private double satisfaction;
	
	private int POSSIBLE_ACTIVITY_SET_PRINTING_SIZE = 6;
	private int PREVIOUS_ACTIVITY_SET_PRINTING_SIZE = 4;
	
	/**
	 * Constructor for the CSV log
	 * @param allActivities:		full set of system activities
	 * @param farmId:				ID of the farm
	 * @param year:					which simulation time step
	 * @param learning_rate:		learning rate for the agent
	 * @param activity_diss:
	 * @param income_diss:
	 * @param satisfaction:
	 * @param strat:				strategy (set of four possible)
	 * @param income:				income of farm
	 * @param currentActivity:		current activity(ies) in system
	 * @param possibleActivities:	all possible activities
	 * @param farm:					specific farm for this decision object
	 * @param modelName:			String name of model
	 */
	public ABMTimeStepLog(String modelName, List<String> allActivities, String farmId, Integer year, Double learning_rate, Double activity_diss, Double income_diss, double satisfaction, int strat, double income, List<Activity> currentActivity, List<String> possibleActivities, Farm farm) {
		setFarmId(farmId);
		setYear(year);
		setStrategy(strat);
		setIncome(income);
		setCurrentActivity(currentActivity);
		setPossibleActivity(possibleActivities);
		setAllActivity(allActivities);
		setLearningRate(learning_rate);
		setFarm(farm);
		setIncome_diss(income_diss);
		setActivity_diss(activity_diss);
		setSatisfaction(satisfaction);
		
		if (modelName.equals("WEEDCONTROL")) {
			PREVIOUS_ACTIVITY_SET_PRINTING_SIZE = 1;
			POSSIBLE_ACTIVITY_SET_PRINTING_SIZE = 9;
		}
		else {
			PREVIOUS_ACTIVITY_SET_PRINTING_SIZE = 4;
			POSSIBLE_ACTIVITY_SET_PRINTING_SIZE = 6;
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
		
		String filename = fileName + "_parameters";
		
		File file = new File(String.format("./output/%s.csv", filename));
		FileWriter fw = null;
		try {
			fw = new FileWriter(file,true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter writer = new PrintWriter(bw);
		
		String name = "year,name,age,education,memory,alpha_plus,alpha_minus,lambda,phi_plus,phi_minus,reference_income,aspiration,"
				+ "beta_l,beta_s,beta_p,tolerance_activity,tolerance_income,activity_dissimilarity,income_dissimilarity,learning_rate,satisfaction," 
				+ "income,strategy,";
		
		for(int i = 0; i < PREVIOUS_ACTIVITY_SET_PRINTING_SIZE; i++) {
			name = name + String.format("previous_activity_%s,",  i+1 );
		}

		for(int i = 0; i < POSSIBLE_ACTIVITY_SET_PRINTING_SIZE -1 ; i++) {
			name = name + String.format("possible_activity_%s,",  i+1 );
		}
		
		name = name + String.format("possible_activity_%s",  POSSIBLE_ACTIVITY_SET_PRINTING_SIZE );          // print last element without comma
		
		if (file.length() == 0) {
			writer.println(name);
		}
		
		writer.print(String.format("%s,",this.year));
		writer.print(String.format("%s,",this.getFarmId()));
		
		writer.print( String.format("%s,",this.farm.getAge()) );
		writer.print( String.format("%s,",this.farm.getEducation() ) );
		writer.print( String.format("%s,",this.farm.getMemory() ) );
		
		writer.print(String.format("%s,",this.farm.getP_alpha_plus()));
		writer.print(String.format("%s,",this.farm.getP_alpha_minus()));
		writer.print(String.format("%s,",this.farm.getP_lambda()));
		writer.print(String.format("%s,",this.farm.getP_phi_plus() ));
		writer.print(String.format("%s,",this.farm.getP_phi_minus() ));
		writer.print(String.format("%s,",this.farm.getP_reference_income() ));
		writer.print(String.format("%s,",this.farm.getP_aspiration_coef() ));
		writer.print(String.format("%s,",this.farm.getP_beta_l() )); 
		writer.print(String.format("%s,",this.farm.getP_beta_s() ));
		writer.print(String.format("%s,",this.farm.getP_beta_p() ));
		
		writer.print(String.format("%s,",this.farm.getP_activity_tolerance_coef() ));
		writer.print(String.format("%s,",this.farm.getP_income_tolerance_coef() ));
		writer.print(String.format("%.4f,", this.getActivity_diss() ) );
		writer.print(String.format("%.4f,", this.getIncome_diss() ) );
		
		writer.print(String.format("%.4f,", this.getLearningRate() ) );
		writer.print(String.format("%.4f,", this.getSatisfaction() ) );
		
		writer.print(String.format("%.2f,",this.income ) );
		writer.print(String.format("%s,",this.strategy) );
		
		// if previous activity set is larger than printing limit, print NA for all options
		if(this.currentActivity.size() == 0 || this.currentActivity.size() > PREVIOUS_ACTIVITY_SET_PRINTING_SIZE) {
			for(int i = 0; i < PREVIOUS_ACTIVITY_SET_PRINTING_SIZE-1 ; i++) {
				writer.print("NA," );
			}
		}
		
		// if previous activity set is smaller than printing limit, print those activities plus NA if required
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
		
		// if there are no possible activities or more than we want to print, print NA for all places
		if (this.possibleActivity.size() == 0 || this.possibleActivity.size() > POSSIBLE_ACTIVITY_SET_PRINTING_SIZE) {
			for(int i = 0; i < POSSIBLE_ACTIVITY_SET_PRINTING_SIZE-1 ; i++) {
				writer.print("NA," );
			}
			writer.print("NA" );
		}
		
		// if we have the exact amount print them, and print the last one without a comma 
		else if (this.possibleActivity.size() == POSSIBLE_ACTIVITY_SET_PRINTING_SIZE){
			for(int i = 0; i < POSSIBLE_ACTIVITY_SET_PRINTING_SIZE-1; i++) {
				writer.print(String.format("%s,", this.possibleActivity.get(i)) );
			}
				writer.print(String.format("%s", this.possibleActivity.get(POSSIBLE_ACTIVITY_SET_PRINTING_SIZE-1)) );
		}
		
		// possible activity set is smaller than largest possible, but not empty (ie 2 possible activities with a maximum print size of 10)
		// in this case print the two possible activities and then print NA for the following with the last NA without a comma
		else {
			int NA_count = POSSIBLE_ACTIVITY_SET_PRINTING_SIZE -  this.possibleActivity.size();
			
			for(int i = 0; i < this.possibleActivity.size(); i++) {
				writer.print(String.format("%s,", this.possibleActivity.get(i)) );
			}
			
			for(int i = 0; i < NA_count-1 ; i++) {
				writer.print("NA," );
			}
			writer.print("NA" );
		}
		
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
	public double getIncome() {
		return income;
	}
	public void setIncome(double income) {
		this.income = income;
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
	public double getLearningRate() {
		return learning_rate;
	}
	public void setLearningRate(double k) {
		this.learning_rate = k;
	}
	public Farm getFarm() {
		return farm;
	}
	public void setFarm(Farm farm) {
		this.farm = farm;
	}

	public double getActivity_diss() {
		return activity_diss;
	}

	public void setActivity_diss(double activity_diss) {
		this.activity_diss = activity_diss;
	}

	public double getIncome_diss() {
		return income_diss;
	}

	public void setIncome_diss(double income_diss) {
		this.income_diss = income_diss;
	}

	public double getSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(double satisfaction) {
		this.satisfaction = satisfaction;
	}
	

}
