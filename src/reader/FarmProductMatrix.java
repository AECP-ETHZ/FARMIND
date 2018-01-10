package reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * Contains a name and ID list for all products with a matching hashmap for each individual farm with the matching products
 * row 1: Product Name...
 * row 2: Farm1: [values for each product] could be preference value or years of experience
 * Each farm contains a copy of this list. 
 * @author kellerke
 *
 */

public class FarmProductMatrix {
	private List<String> productName = new ArrayList<String>();
	private Map<String,Integer[]> productMap = new HashMap<String,Integer[]>();

	/** 
	 * Given a farmID and a product, return the value of that matrix cell
	 * 
	 * @param FarmID
	 * @param Product
	 * @return value of that cell in the matrix
	 */
	public int farmProductValue(String FarmID, String Product) {
		int val;
		int index = productName.indexOf(Product);
		val = productMap.get(FarmID)[index];	 							   // get(FarmID) returns int array
		return val;
	}
	
	public List<String> getProductName() {
		return productName;
	}

	public void setProductName(List<String> productName) {
		this.productName = productName;
	}
	
	public Map<String,Integer[]> getProductmap() {
		return productMap;
	}
	
	/**
	 * Create the actual product cost lists
	 * @param matrixRow is an array list starting with a farm name and continuing with integer costs
	 */
	public void setProductMap(ArrayList<String> matrixRow) {
		String name = matrixRow.get(0);
		Integer[] values = new Integer[matrixRow.size() - 1];
		
		for (int i = 1; i < matrixRow.size(); i++) {
			values[i-1] = Integer.valueOf(matrixRow.get(i));
		}
		
		this.productMap.put(name, values);
	}

	public void setProductMap(Map<String, Integer[]> localProductMap) {
		this.productMap = localProductMap;
	}
	
}
