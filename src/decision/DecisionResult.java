package decision;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import activity.Activity;
import agent.Farm;

/**
 * Decision object with unique information for each farm. This object can output a gams compatible simulation file, as well as output a log of all decisions and parameters during the model execution. 
 * This object contains a public 2d array of the pre/post sowing strategies that correspond to the 72 strategies in the model. 
 * @author kellerke
 *
 */
public class DecisionResult {

	private String farmId;													   // unique farm id
	private Integer year;													   // which time step this decision was made in
	private int strategy;													   // farm strategy
	private double income;													   // income of time step
	private double learning_rate;											   // learning rate of agent
	private List<String> allActivity;										   // All possible activities in the model
	private List<Activity> currentActivity;									   // current activity of the agent
	private List<String> possibleActivity;								       // set of possible activities by the agent
	private Farm farm;														   // farm holds parameters
	
	/** 
	 * Constructor for the Decision Result
	 * @param allActivities	full set of activities
	 * @param farmId		ID of the farm
	 * @param possibleActivities		full activity set
	 * @param year			time period
	 * @param param			which parameters were used
	 * @param strat			strategy
	 * @param currentActivity		current activity(ies) in system
	 * @param income		income of farm
	 */
	public DecisionResult(List<String> allActivities, String farmId, Integer year, Double learning_rate, int strat, double income, List<Activity> currentActivity, List<String> possibleActivities, Farm farm) {
		setFarmId(farmId);
		setYear(year);
		setStrategy(strat);
		setIncome(income);
		setCurrentActivity(currentActivity);
		setPossibleActivity(possibleActivities);
		setAllActivity(allActivities);
		setLearningRate(learning_rate);
		setFarm(farm);
	}

	/** 
	 * Create the files that contain the parameters required to run the gams simulation. <br>
	 * Use the strategy matrix variable that is 55x6 elements and set a specific bit pattern based on the selected strategy. Each strategy corresponds to a StrategySet tuple value which sets the correct 1 in the matrix.
	 * 
	 */
	public void appendMPInput() {
		File file = new File("p_allowedStratPrePost.csv");
		int[][] output = activity_matrix;									   // copy empty matrix
		
		try {
			FileWriter fw = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter writer = new PrintWriter(bw);
			if (file.length() == 0) {
				writer.println(",,spre1,spre2,spre3,spre4,spre5,spre6");
			}
						
			for(int i = 1; i < activitySets.length + 1; i++) {
				if (this.possibleActivity.contains(String.format("strat%d", i))) {
					int[] ind = activitySets[i-1];
					int row = ind[0];
					int column = ind[1];
					output[row-1][column-1] = 1;							   // set correct bit to 1 in output matrix if this strategy is selected
				}
			}
			
			for(int i = 1; i < 56; i++) {	
				int[] row = output[i-1];									   // print each output row to build full gams file
				String name = farmId.substring(1, farmId.length() - 1);
				if (name.charAt(0) == '\\') {
					name = name.substring(1, name.length() - 1);               // in the input csv files, we use a \ to indicate a " in the output name. This is a workaround for an ugly issue with csv file input in R.
					name = "\"" + name + "\"";
				}
				
				writer.println(String.format("%s,spost%d,%d,%d,%d,%d,%d,%d", name, i, row[0],row[1],row[2],row[3],row[4],row[5]));
			}
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
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
		
		File file = new File(String.format("./output/%s.csv", fileName));
		FileWriter fw = null;
		try {
			fw = new FileWriter(file,true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter writer = new PrintWriter(bw);
		
		if (file.length() == 0) {
			writer.println("year,name,age,education,memory,alpha_plus,alpha_minus,lambda,phi_plus,phi_minus,aspiration_coef,tolerance_activity,learning_rate,tolerance_income,beta,beta_s,strategy,possible_activity1,"
					+ "possible_activity2,possible_activity3,possible_activity4,possible_activity5,possible_activity6,income,current_activity");
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
		writer.print(String.format("%s,",this.farm.getP_aspiration_coef() ));
		writer.print(String.format("%s,",this.farm.getP_activity_tolerance_coef() ));
		writer.print(String.format("%.2f,", this.getLearningRate() ) );
		writer.print(String.format("%s,",this.farm.getP_income_tolerance_coef() ));
		writer.print(String.format("%s,",this.farm.getP_beta() ));
		writer.print(String.format("%s,",this.farm.getP_beta_s() ));
		writer.print(String.format("%s,",this.strategy) );
		
		for(int i = 0; i < this.possibleActivity.size(); i++) {
			//writer.print(String.format("%d,",  1 + this.allActivity.indexOf( this.possibleActivity.get(i)) ) );
			writer.print(String.format("%s,",   this.possibleActivity.get(i)) );
		}
		
		for(int i = 0; i < 6 - this.possibleActivity.size(); i++) {
			writer.print("NA," );
		}

		writer.print(String.format("%s,",this.income ) );
		
		for(int i = 0; i < this.currentActivity.size(); i++) {
			//writer.print(String.format("%d,",  1 + this.allActivity.indexOf(this.currentActivity.get(i).getName() )) );
			if (i < this.currentActivity.size() - 1) {
				writer.print(String.format("%s,",  this.currentActivity.get(i).getName()) );
			}
			if (i == this.currentActivity.size() - 1) {
				writer.print(String.format("%s",  this.currentActivity.get(i).getName()) );
			}
		}
		
		writer.print("\n");
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
	
	/**
	 *  We have 72 activities in the system, and these tuples correspond to each activity.  first element in the tuple is a row in the activity matrix, and the second element is the column.
	 *  Each row element corresponds to a post sowing strategy, and each column is a pre sowing strategy. So [53,2] corresponds to post_strat 53, and pre_strat 2 strategy set.  
	 */
	public static int[][] activitySets = 
		{
				{1,1},												           // activity 1 is post sowing 1, pre sowing 1											    
				{1,2},														   // activity 2 is post 1, pre 2
				{1,3},														   // activity 3 is post 1, pre 3
				{1,4},													       // activity 4 is post 1, pre 4
				{1,5},														   // activity 5 is post 1, pre 5
				{1,6},														   // activity 6 is post 1, pre 6
				{3,2},														   // activity 7 is post 3, pre 2
				{3,3},														   // activity 8 is post 3, pre 2
				{3,4},
				{3,5},
				{3,6},
				{5,3},
				{5,4},
				{5,5},
				{7,3},
				{7,4},
				{7,5},
				{12,2},
				{12,3},
				{12,4},
				{12,5},
				{12,6},
				{13,3},
				{13,4},
				{13,5},
				{14,3},
				{14,4},
				{14,5},
				{16,3},
				{16,4},
				{16,5},
				{18,2},
				{18,3},
				{18,4},
				{18,5},
				{18,6},
				{21,3},
				{21,4},
				{21,5},
				{22,3},
				{22,4},
				{22,5},
				{23,2},
				{23,3},
				{23,6},
				{28,2},
				{28,3},
				{28,6},
				{33,2},
				{33,3},
				{33,6},
				{36,3},
				{36,4},
				{36,5},
				{37,3},
				{37,4},
				{37,5},
				{39,3},
				{39,4},
				{39,5},
				{51,2},
				{51,3},
				{51,4},
				{52,2},
				{52,3},
				{52,4},
				{53,2},
				{53,3},
				{53,4},
				{54,2},
				{54,3},
				{54,4}														   // strategy 72, post sowing 54, pre sowing 4
		};
	
	/**
	 *  empty matrix for the output gams file. The matrix corresponds to a 55 row by 6 column matrix where each row is a post strategy and the columns are a pre strategy 
	 */
	public static int[][] activity_matrix = 
		{
				{1,1,1,1,1,1},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0}
		};
	
}
