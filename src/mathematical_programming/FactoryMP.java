package mathematical_programming;

import java.util.Properties;

public class FactoryMP {
	private MP_Interface MP;
	
	public FactoryMP(Properties cmd, int simYear, int memoryLengthAverage) {
		if (cmd.getProperty("modelName").equals("WEEDCONTROL")) {
			setMP(new WeedControl(cmd, simYear, memoryLengthAverage));
		} 
		if (cmd.getProperty("modelName").equals("SWISSLAND")) {
			setMP(new SwissLand(cmd));
		}
		
		if (cmd.getProperty("modelName").equals("FARMDYN")) {
			setMP(new FarmDyn(cmd, simYear, memoryLengthAverage));
		}
	}

	public MP_Interface getMP() {
		return MP;
	}

	public void setMP(MP_Interface mP) {
		MP = mP;
	}
	
	
}
