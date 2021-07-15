package mathematical_programming;

import java.io.IOException;
import java.util.Properties;

/** 
 * Factory class to generate the correct version of the optimization model based on the config.properties file. 
 * 
 * @author kellerke
 */
public class FactoryMP {
	private MP_Interface MP;
	
	/**
	 * Given the properties generate the correct MP model for use during the simulation
	 * @param cmd :: all properties of the model
	 * @param simYear :: how many years to use for simulation
	 * @param memoryLengthAverage :: average memory length of input data files
	 * @throws IOException 
	 */
	public FactoryMP(Properties cmd, int simYear, int memoryLengthAverage) throws IOException {
		if (cmd.getProperty("modelName").equals("WEEDCONTROL")) {
			setMP(new WeedControl(cmd, simYear, memoryLengthAverage));
		} 
		if (cmd.getProperty("modelName").equals("SWISSLAND")) {
			setMP(new SwissLand(cmd));
		}
		if (cmd.getProperty("modelName").equals("FARMDYN")) {
			setMP(new FarmDyn(cmd, simYear, memoryLengthAverage));
		}
		if (cmd.getProperty("modelName").equals("TOY")) {
			setMP(new Toy(cmd, simYear, memoryLengthAverage));
		}
	}

	public MP_Interface getMP() {
		return this.MP;
	}

	public void setMP(MP_Interface mP) {
		this.MP = mP;
	}
	
	
}
