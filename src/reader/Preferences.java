package reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * This contains all product preferences for all the farms in the system.
 * Each farm is given a copy of this object 
 * @author kellerke
 *
 */

public class Preferences {
	
	// First row gives the ID
	// Second row gives the titles
	private List<String> preferencesID = new ArrayList<String>();
	private List<String> preferencesName = new ArrayList<String>();
	private Map<String,Integer[]> preferencesMap = new HashMap<String,Integer[]>();

	public List<String> getpreferencesID() {
		return preferencesID;
	}
	
	/** 
	 * 
	 * @param preferencesID is the list array of all the products in the system
	 */
	public void setPreferencesID(List<String> preferencesID) {
		this.preferencesID = preferencesID;
	}	
	
	public List<String> getPreferencesName() {
		return preferencesName;
	}

	public void setPreferencesName(List<String> preferencesName) {
		this.preferencesName = preferencesName;
	}
	
	public Map<String,Integer[]> getpreferencesMap() {
		return preferencesMap;
	}
	
	/**
	 * Create the actual product cost lists
	 * @param matrixRow is an array list starting with a farm name and continuing with integer costs
	 */
	public void setPreferencesMap(ArrayList<String> matrixRow) {
		String name = matrixRow.get(0);
		Integer[] values = new Integer[matrixRow.size() - 1];
		
		for (int i = 1; i < matrixRow.size(); i++) {
			values[i-1] = Integer.valueOf(matrixRow.get(i));
		}
		
		this.preferencesMap.put(name, values);
	}

	public void setPreferencesMap(Map<String, Integer[]> localpreferencesMap) {
		this.preferencesMap = localpreferencesMap;
	}
	
}
