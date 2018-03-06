package decision;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import activity.Activity;
import reader.Parameters;

/**
 * Decision object with unique information for each farm. 
 * Can be used to append to the final output list
 * @author kellerke
 *
 */
public class DecisionResult {

	private String farmId;													   // unique farm id
	private Integer year;													   // which time step this decision was made in
	private Parameters param;												   // which parameter set was used
	private int strategy;													   // farm strategy
	private double income;													   // income of time step
	private List<String> allActivity;										   // All possible activities in the model
	private List<Activity> currentActivity;
	private List<String> possibleActivity;
	
	/** 
	 * Constructor for the Decision Result
	 * @param allActivities	full set of activities
	 * @param farmId		ID of the farm
	 * @param possibleActivities		full product set
	 * @param year			time period
	 * @param param			which parameters were used
	 * @param strat			strategy
	 * @param currentActivities		current actions in system
	 * @param income		income of farm
	 */
	public DecisionResult(List<String> allActivities, String farmId, Integer year, Parameters param, int strat, double income, List<Activity> currentActivities, List<String> possibleActivities) {
		setFarmId(farmId);
		setYear(year);
		setParam(param);
		setStrategy(strat);
		setIncome(income);
		setCurrentActivity(currentActivities);
		setPossibleActivity(possibleActivities);
		setAllActivity(allActivities);
	}

	/** 
	 * Create the files that contain the parameters required to run the gams simulation.
	 * Use the prebuilt 'options' matrix that is 55x6 elements and set a specific bit pattern based on the selected strategy.
	 * Each strategy corresponds to a index value which sets the correct 1 in the matrix.
	 */
	public void appendGamsFile() {
		File file = new File("p_allowedStratPrePost.csv");
		int[][] output = options;									       // copy empty matrix
		
		try {
			FileWriter fw = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter writer = new PrintWriter(bw);
			if (file.length() == 0) {
				writer.println(",,spre1,spre2,spre3,spre4,spre5,spre6");
			}
						
			for(int i = 1; i < 67; i++) {
				if (this.possibleActivity.contains(String.format("strat%d", i))) {
					int[] ind = index[i-1];
					int row = ind[0];
					int column = ind[1];
					output[row-1][column-1] = 1;							   // set proper bit to 1 if this strategy is selected
				}
			}
			
			for(int i = 1; i < 56; i++) {	
				int[] row = output[i-1];									   // print each output row to build full gams file
				writer.println(String.format("%s,spost%d,%d,%d,%d,%d,%d,%d", farmId, i, row[0],row[1],row[2],row[3],row[4],row[5]));
			}
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * write output CSV file based on decision object
	 * @param fileName of output file
	 */
	public void appendDecisionFile(String fileName) {
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
			writer.println("year,name,alpha_plus,alpha_minus,lambda,phi_plus,phi_minus,a,b,k,strategy,possible_action1,"
					+ "possible_action2,possible_action3,possible_action4,possible_action5,possible_action6,income,current_action1");
		}
		
		writer.print(String.format("%s,",this.year));
		writer.print(String.format("%s,",this.farmId));
		writer.print(String.format("%s,",this.param.getAlpha_plus()));
		writer.print(String.format("%s,",this.param.getAlpha_minus()));
		writer.print(String.format("%s,",this.param.getLambda()));
		writer.print(String.format("%s,",this.param.getPhi_plus() ));
		writer.print(String.format("%s,",this.param.getPhi_minus() ));
		writer.print(String.format("%s,",this.param.getA() ));
		writer.print(String.format("%s,",this.param.getB() ));
		writer.print(String.format("%s,",this.param.getK() ));
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
			writer.print(String.format("%s,",  this.currentActivity.get(i).getName()) );
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
	public Parameters getParam() {
		return param;
	}
	public void setParam(Parameters param) {
		this.param = param;
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
	
	// We have 66 strategies in the system, and these tuples correspond to each strategy
	// first element in the tuple is a row
	// second element is the column
	// set the corresponding bit in the output matrix to 1 for each strategy
	private int[][] index = 
		{
				{3,2},
				{3,3},
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
				{54,4}
		};
	
	// empty matrix for the output file
	// note first row should always be 1s
	private int[][] options = 
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
