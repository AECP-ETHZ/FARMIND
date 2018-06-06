package reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * The FarmDataMatrix object is a reference object for each agent that stores the individual farm data. 
 * The FarmDataMatrix is an n*m matrix that contains the reference data (eg. individual activity) in the rows, and each column is an individual farm. 
 * row 1: Vector of data element names (eg. activities in system) <br>
 * row 2: Farm1: [values for each associated data element]  <br>
 * row 3: Farm2: [values for each associated data element] <br>
 * row m: FarmM: [values for each associated data element] <br>
 * These data elements for could be activity preference or years of farming experience<br>
 * @author kellerke
 */

public class FarmDataMatrix {
	private List<String> dataElementNames = new ArrayList<String>();				   	 // top row of matrix with names of the data elements
	private Map<String,Integer[]> elementFarmMap = new HashMap<String,Integer[]>();	     // map that links farm and data with a value

	/** 
	 * Given a farmID and a name of the data element (eg. activity), return the value of that matrix cell. 
	 * @param FarmID of the specific farm
	 * @param data element name to return value from matrix
	 * @return value of that cell in the matrix
	 */
	public int getFarmDataElementValue(String FarmID, String name) {
		int val;
		int index = dataElementNames.indexOf(name);
		val = elementFarmMap.get(FarmID)[index];	 						   // get(FarmID) returns int array
		return val;
	}
	
	/** 
	 * Given a farmID and a name, set the value of that cell in the matrix. 
	 * 
	 * @param FarmID of the specific farm
	 * @param data element name (eg. activity name)
	 * @param value to set for farm and element combination
	 */
	public void setFarmDataElementValue(String FarmID, String name, int value) {
		int index = dataElementNames.indexOf(name);
		Integer[] array = elementFarmMap.get(FarmID);
		array[index] = value;
		
		elementFarmMap.replace(FarmID, array);
	}
	
	public List<String> getDataElementName() {
		return dataElementNames;
	}

	public void setDataElementName(List<String> listNames) {
		this.dataElementNames = listNames;
	}
	
	public Map<String,Integer[]> getFarmMap() {
		return elementFarmMap;
	}
	
	/**
	 * Set the row of data that corresponds with the specific farm name. 
	 * @param matrixRow is an array list starting with a farm name and continuing with integer costs
	 */
	public void setFarmMap(ArrayList<String> matrixRow) {
		String name = matrixRow.get(0);
		Integer[] values = new Integer[matrixRow.size() - 1];
		
		for (int i = 1; i < matrixRow.size(); i++) {
			values[i-1] = Integer.valueOf(matrixRow.get(i));
		}
		
		this.elementFarmMap.put(name, values);
	}

	public void setDataElementMap(Map<String, Integer[]> dataMap) {
		this.elementFarmMap = dataMap;
	}
	
}
