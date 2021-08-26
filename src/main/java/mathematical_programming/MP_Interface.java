package mathematical_programming;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import activity.Activity;
import agent.Farm;

/** 
 * Contains the generic functions to allow the possible activities from the decision making process to be exported to the mathematical programming (MP) model. 
 * 
 * @author kellerke
 */
public interface MP_Interface {
	
	/**
	 * Generate the required input files for the gams MP model
	 * @param farm :: farm object
	 * @param possibleActivity :: list of possible activities that this farm will send to the gams MP model
	 * @throws IOException 
	 */
	public void inputsforMP(Farm farm, List<String> possibleActivity) throws IOException;
	
	/**
	 * Run the gams MP model based on the configuration we set in the inputsforMP model 
	 * @param cmd :: command object based on control.properties file
	 * @param nFarms :: how many farms we run in the system
	 * @param year :: what simulation year we are testing (can change the run if it's the first iteration)
	 * @param pricingAverage :: used in weedcontrol to set average pricing of wheat over previous iterations
	 * @param memoryLengthAverage :: used to set how long agents remember (use average across group despite possible inter-farm variations)
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void runModel(Properties cmd, int nFarms, int year, boolean pricingAverage, int memoryLengthAverage) throws FileNotFoundException, IOException;
	
	/** 
	 * Read results from MP model with income of each agent
	 * @param cmd :: command object based on control.properties file 
	 * @param allFarms :: full list of farm agents in system
	 * @return :: return income list
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public List<Double> readMPIncomes(Properties cmd, List<Farm> allFarms) throws FileNotFoundException, IOException;
	
	/** 
	 * Read results from MP model with activities of each agent
	 * @param cmd :: command object based on control.properties file 
	 * @param allFarms :: full list of farm agents in system
	 * @return :: return activity list
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public List<ArrayList<Activity>> readMPActivities(Properties cmd, List<Farm> allFarms) throws FileNotFoundException, IOException;
	
	/**
	 * If agent leaves system then we set an exit activity. We want to keep the agent in the system, we just don't want further calculations for this agent
	 * @return :: activity list of exit activities for the specific MP model and agent
	 */
	public ArrayList<Activity> getExitActivity();
}
