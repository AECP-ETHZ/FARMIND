package reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import product.Crop;
import product.Livestock;

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
	
	public Integer getTechDistance(String p1, String p2, List<Crop> crops, List<Livestock> livestock) {
		int distance = 0;
		List<String> cropName = new ArrayList<String>();
		List<Integer> cropID = new ArrayList<Integer>();
		List<String> liveName = new ArrayList<String>();
		List<Integer> liveID = new ArrayList<Integer>();
		
		// get list of names and ID values to compare
		for (int i = 0; i<crops.size(); i++) {
			cropName.add(crops.get(i).getName());
			cropID.add(crops.get(i).getID());
		}
		
		for (int i = 0; i<livestock.size(); i++) {
			liveName.add(livestock.get(i).getName());
			liveID.add(livestock.get(i).getID());
		}
		
		// if product types are different than 10
		if (liveName.contains(p1) && !liveName.contains(p2))
		{
			distance = 10;
		}
		else if (cropName.contains(p1) && !cropName.contains(p2))
		{
			distance = 10;
		}
		
		// if both crop or both livestock than check ID values
		else if (cropName.contains(p1) && cropName.contains(p2))
		{
			int index = cropName.indexOf(p1);
			double d1 = cropID.get(index);
			index = cropName.indexOf(p2);
			double d2 = cropID.get(index);
			
			if ( Math.abs(d1 - d2) > 1000.00) {
				distance = 4;
			} else if ( Math.abs(d1 - d2) > 100.00) {
				distance = 3;
			}
			else if ( Math.abs(d1 - d2) > 10.00) {
				distance = 3;
			} else {
				distance = 1;
			}
		}
		
		else if (liveName.contains(p1) && liveName.contains(p2))
		{
			int index = liveName.indexOf(p1);
			double d1 = liveID.get(index);
			index = liveName.indexOf(p2);
			double d2 = liveID.get(index);
			
			if ( Math.abs(d1 - d2) > 1000.00) {
				distance = 4;
			} else if ( Math.abs(d1 - d2) > 100.00) {
				distance = 3;
			}
			else if ( Math.abs(d1 - d2) > 10.00) {
				distance = 3;
			} else {
				distance = 1;
			}
		}
		
		return distance;
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
