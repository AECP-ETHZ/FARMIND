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
 * 
 * @author kellerke
 */
public class ABMTimeStepLog {

    private final String farmId;                                               // unique farm id
    private final Integer year;                                                // which time step this decision was made in
    private final int strategy;                                                // farm strategy
    private final double income;                                               // income of time step
    private final double learning_rate;                                        // learning rate of agent
    private final List<Activity> currentActivity;                              // current activity of the agent
    private final List<String> possibleActivity;                               // set of possible activities by the agent
    private final Farm farm;                                                   // farm holds parameters
    private final double activity_diss;                                        // activity diss for agent
    private final double income_diss;                                          // income diss for agent
    private final double satisfaction;                                         // satisfaction for agent
    
    private int POSSIBLE_ACTIVITY_SET_PRINTING_SIZE = 6;                       // limit printable information so we don't flood the log
    private int PREVIOUS_ACTIVITY_SET_PRINTING_SIZE = 4;
    
    /**
     * Constructor for the CSV log
     * @param modelName ::          String name of model
     * @param year ::                which simulation time step
     * @param possibleActivities ::    all possible activities
     * @param farm ::                specific farm for this decision object
     */
    public ABMTimeStepLog(String modelName, Integer year, final List<String> possibleActivities, final Farm farm) {
        this.farmId = farm.getFarmName();
        this.year = year;
        this.strategy = farm.getStrategy();
        this.income = farm.getIncomeHistory().get(0);
        this.currentActivity = farm.getCurrentActivity();
        this.possibleActivity = possibleActivities;
        this.learning_rate = farm.getLearningRate();
        this.farm = farm;
        this.income_diss = farm.getIncome_Dissimilarity();
        this.activity_diss = farm.getActivity_Dissimilarity();
        this.satisfaction = farm.getSatisfaction();
        
        switch (modelName) {
            case "WEEDCONTROL": 
                this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE = 1;
                this.POSSIBLE_ACTIVITY_SET_PRINTING_SIZE = 9;
                break;
            case "PRECALCULATED":
                this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE = 5;
                this.POSSIBLE_ACTIVITY_SET_PRINTING_SIZE = 5;
                break;
            default:
                this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE = 4;
                this.POSSIBLE_ACTIVITY_SET_PRINTING_SIZE = 6;
                break;
        }
    }
    
    /** 
     * write output CSV log file based on decision object. This log file can be updated each time period for each agent. 
     * @param fileName :: output file which is previously checked to ensure we will not exceed 1 million lines of data. 
     * @throws IOException 
     */
    public void appendLogFile(String fileName) throws IOException {
        String PATH = "./output";
        File directory = new File(PATH);
        if(!directory.exists()) {
            directory.mkdir();
        }
        
        File file = new File(String.format("%s/%s_parameters.csv", PATH, fileName));
        boolean appending = file.exists() && file.length() > 0;
        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(new FileWriter(file, true))
        )) {
            
            String name = "year,name,age,education,memory,alpha_plus,alpha_minus,lambda,phi_plus,phi_minus,reference_income,aspiration,"
                    + "beta_l,beta_s,beta_p,tolerance_activity,tolerance_income,activity_dissimilarity,income_dissimilarity,learning_rate,satisfaction," 
                    + "income,strategy";
            
            for(int i = 0; i < this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE; i++) {
                name = name + String.format(",previous_activity_%c", (char)('a' + i));
            }
    
            for(int i = 0; i < this.POSSIBLE_ACTIVITY_SET_PRINTING_SIZE; i++) {
                name = name + String.format(",possible_activity_%c", (char)('a' + i));
            }
            
            if (!appending) {
                writer.println(name);
            }
            
            writer.print(String.format("%s,",this.year));
            writer.print(String.format("%s,",this.farmId));
            
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
            writer.print(String.format("%.4f,", this.activity_diss ) );
            writer.print(String.format("%.4f,", this.income_diss ) );
            
            writer.print(String.format("%.4f,", this.learning_rate ) );
            writer.print(String.format("%.4f,", this.satisfaction ) );
            
            writer.print(String.format("%.2f,",this.income ) );
            writer.print(String.format("%s,",this.strategy) );
            
            // if previous activity set is larger than printing limit, print NA for all options
            if(this.currentActivity.size() == 0 || this.currentActivity.size() > this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE) {
                for(int i = 0; i < this.PREVIOUS_ACTIVITY_SET_PRINTING_SIZE-1 ; i++) {
                    writer.print("NA," );
                }
            }
            
            // if previous activity set is smaller than printing limit, print those activities plus NA if required
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
            
            // if there are no possible activities or more than we want to print, print NA for all places
            if (this.possibleActivity.size() == 0 || this.possibleActivity.size() > this.POSSIBLE_ACTIVITY_SET_PRINTING_SIZE) {
                for(int i = 0; i < this.POSSIBLE_ACTIVITY_SET_PRINTING_SIZE-1 ; i++) {
                    writer.print("NA," );
                }
                writer.print("NA" );
            }
            
            // if we have the exact amount print them, and print the last one without a comma 
            else if (this.possibleActivity.size() == this.POSSIBLE_ACTIVITY_SET_PRINTING_SIZE){
                for(int i = 0; i < this.POSSIBLE_ACTIVITY_SET_PRINTING_SIZE-1; i++) {
                    writer.print(String.format("%s,", this.possibleActivity.get(i)) );
                }
                    writer.print(String.format("%s", this.possibleActivity.get(this.POSSIBLE_ACTIVITY_SET_PRINTING_SIZE-1)) );
            }
            
            // possible activity set is smaller than largest possible, but not empty (ie 2 possible activities with a maximum print size of 10)
            // in this case print the two possible activities and then print NA for the following with the last NA without a comma
            else {
                int NA_count = this.POSSIBLE_ACTIVITY_SET_PRINTING_SIZE -  this.possibleActivity.size();
                
                for(int i = 0; i < this.possibleActivity.size(); i++) {
                    writer.print(String.format("%s,", this.possibleActivity.get(i)) );
                }
                
                for(int i = 0; i < NA_count-1 ; i++) {
                    writer.print("NA," );
                }
                writer.print("NA" );
            }
            
            writer.println("");
        }
    }
}
