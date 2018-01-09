package transactioncost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Transaction Cost Matrix is created based on an external CSV file. 
 * Query the matrix object with two product strings and the cost between those products can be returned. 
 * @author kekeller
 *
 */

public class TCMatrix {
	
	// First row gives the index
	private List<String> productIndex = new ArrayList<String>();
	private Map<String,Integer[]> productMap = new HashMap<String,Integer[]>();
	
	/**
	 * Cost between two input products in the transaction cost matrix
	 * 
	 * @param p1 product 1 to compare
	 * @param p2 product 2 to compare
	 * @return cost between product 1 and product 2
	 */
	public Integer getCost(String p1, String p2) {
		int index = 0;
		Integer[] values;
		int cost = 0;
		index = productIndex.indexOf(p1);
		
		values = productMap.get(p2);
		
		cost = values[index];
		return cost;
	}
	
	public List<String> getProductIndex() {
		return productIndex;
	}
	
	/** 
	 * 
	 * @param productIndex is the list array of all the products in the system
	 */
	public void setProductIndex(List<String> productIndex) {
		this.productIndex = productIndex;
	}	
	
	public Map<String,Integer[]> getProductMap() {
		return productMap;
	}
	
	/**
	 * Create the actual product cost lists
	 * @param matrixRow is an array list starting with a product and continuing with integer costs
	 */
	public void setProductMap(ArrayList<String> matrixRow) {
		String product = matrixRow.get(0);
		Integer[] values = new Integer[matrixRow.size() - 1];
		
		for (int i = 1; i < matrixRow.size(); i++) {
			values[i-1] = Integer.valueOf(matrixRow.get(i));
		}
		
		this.productMap.put(product, values);
	}
}
