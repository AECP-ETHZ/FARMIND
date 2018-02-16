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
	private List<String> activities;										   // activity list
	private Integer year;													   // which time step this decision was made in
	private Parameters param;												   // which parameter set was used
	private int strategy;													   // farm strategy
	private List<Activity> currentActions;									   // optimizer activity set
	private double income;													   // income of time step
	private List<String> productNames;										   // full activity list
	
	/** 
	 * 
	 * @param productNames	full set of activities
	 * @param farmId		ID of the farm
	 * @param maxSet		full product set
	 * @param year			time period
	 * @param param			which parameters were used
	 * @param strat			strategy
	 * @param currentActions		current actions in system
	 * @param income		income of farm
	 */
	public DecisionResult(List<String> productNames, String farmId, List<String> maxSet, Integer year, Parameters param, int strat, double income, List<Activity> currentActions) {
		setFarmId(farmId);
		setProducts(maxSet);
		setYear(year);
		setParam(param);
		setStrategy(strat);
		setIncome(income);
		setProductNames(productNames);
		setCurrentActions(currentActions);
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
			writer.println("year,name,alpha_plus,alpha_minus,lambda,phi_plus,phi_minus,a,b,k,strategy,fuzzy_action1,"
					+ "fuzzy_action2,fuzzy_action3,fuzzy_action4,fuzzy_action5,fuzzy_action6, income, lp_action1,lp_action2,lp_action3");
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
		
		for(int i = 0; i < this.activities.size(); i++) {
			writer.print(String.format("%d,",  1 + this.productNames.indexOf( this.activities.get(i)) ) );
		}
		
		for(int i = 0; i < 6 - this.activities.size(); i++) {
			writer.print("NA," );
		}

		writer.print(String.format("%s",this.income ) );
		
		for(int i = 0; i < this.currentActions.size(); i++) {
			writer.print(String.format("%d,",  1 + this.productNames.indexOf(this.currentActions.get(i).getName() )) );
		}
		
		for(int i = 0; i < 3 - this.currentActions.size(); i++) {
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
	public List<String> getProducts() {
		return activities;
	}
	public void setProducts(List<String> products) {
		this.activities = products;
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
	public List<String> getProductNames() {
		return productNames;
	}
	public void setProductNames(List<String> productNames) {
		this.productNames = productNames;
	}

	public List<Activity> getCurrentActions() {
		return currentActions;
	}

	public void setCurrentActions(List<Activity> currentActions) {
		this.currentActions = currentActions;
	}

}
