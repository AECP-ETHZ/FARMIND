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
 */

public class FarmDataMatrix {
	private List<String> dataElementNames = new ArrayList<String>();				   	 // top row of matrix with names of the data elements
	private Map<String,Double[]> elementFarmMap = new HashMap<String,Double[]>();	     // map that links farm and data with a value

	/** 
	 * Given a farmID and a name of the data element (eg. activity), return the value of that matrix cell. 
	 * @param FarmID: of the specific farm
	 * @param name: data element name to return value from matrix
	 * @return value of that cell in the matrix
	 */
	public double getFarmDataElementValue(String FarmID, String name) {
		double val;
		int index = dataElementNames.indexOf(name);
		val = elementFarmMap.get(FarmID)[index];	 						   // get(FarmID) returns int array
		return val;
	}
	
	/** 
	 * Given a farmID and a name, set the value of that cell in the matrix. 
	 * 
	 * @param FarmID: of the specific farm
	 * @param name: data element name (e.g. activity name)
	 * @param value: to set for farm and element combination
	 */
	public void setFarmDataElementValue(String FarmID, String name, double value) {
		int index = dataElementNames.indexOf(name);
		Double[] array = elementFarmMap.get(FarmID);
		array[index] = value;
		
		elementFarmMap.replace(FarmID, array);
	}
	
	public List<String> getDataElementName() {
		return dataElementNames;
	}

	public void setDataElementName(List<String> listNames) {
		this.dataElementNames = listNames;
	}
	
	public Map<String,Double[]> getFarmMap() {
		return elementFarmMap;
	}
	
	/**
	 * Set the row of data that corresponds with the specific farm name. 
	 * @param matrixRow is an array list starting with a farm name and continuing with integer costs
	 */
	public void setFarmMap(ArrayList<String> matrixRow) {
		String name = matrixRow.get(0);
		Double[] values = new Double[matrixRow.size() - 1];
		
		for (int i = 1; i < matrixRow.size(); i++) {
			values[i-1] = Double.valueOf(matrixRow.get(i));
		}
		
		this.elementFarmMap.put(name, values);
	}

	public void setDataElementMap(Map<String, Double[]> dataMap) {
		this.elementFarmMap = dataMap;
	}
	
}
