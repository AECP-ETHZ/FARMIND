package mathematical_programming;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static decision.DecisionResult.activitySets;                            
import static decision.DecisionResult.activity_matrix;                            


public class MP implements MP_Interface{
	
	/** 
	 * Create the files that contain the parameters required to run the gams simulation. <br>
	 * Use the strategy matrix variable that is 55x6 elements and set a specific bit pattern based on the selected strategy. Each strategy corresponds to a StrategySet tuple value which sets the correct 1 in the matrix.
	 * 
	 */
	/*
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
	*/

}
