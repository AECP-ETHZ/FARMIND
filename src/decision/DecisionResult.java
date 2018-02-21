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
	 * Create a file that contains the parameters required to run the gams simulation
	 */
	public void appendGamsFile() {
		File file = new File("p_allowedStrat.csv");

		try {
			FileWriter fw = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter writer = new PrintWriter(bw);
			if (file.length() == 0) {
				writer.println("gn,spre,normal,GlyBan");
			}
			
			int gly = 0;
			for(int i = 1; i < 22; i++) {
				if (i == 17) i = i+2;
				String act = String.format("spre%d", i);
				if(possibleActivity.contains(act)) {
					gly = 1;
				} else {gly = 0;}
				if (i == 1) gly = 1;	
				
				writer.println(String.format("%s,spre%d,1,%d", farmId, i, gly));
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
					+ "possible_action2,possible_action3,possible_action4,possible_action5,possible_action6,income,current_action1,current_action2,current_action3");
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
			writer.print(String.format("%d,",  1 + this.allActivity.indexOf( this.possibleActivity.get(i)) ) );
		}
		
		for(int i = 0; i < 6 - this.possibleActivity.size(); i++) {
			writer.print("NA," );
		}

		writer.print(String.format("%s",this.income ) );
		
		for(int i = 0; i < this.currentActivity.size(); i++) {
			writer.print(String.format("%d,",  1 + this.allActivity.indexOf(this.currentActivity.get(i).getName() )) );
		}
		
		for(int i = 0; i < 3 - this.currentActivity.size(); i++) {
			writer.print("NA," );
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
}
