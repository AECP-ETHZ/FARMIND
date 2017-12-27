package transactioncost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCMatrix {
	
	// First row gives the index
	private List<String> productIndex = new ArrayList<String>();
	private Map<String,Integer[]> productMap = new HashMap<String,Integer[]>();
	
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
	public void setProductIndex(List<String> productIndex) {
		this.productIndex = productIndex;
	}	
	
	public Map<String,Integer[]> getProductMap() {
		return productMap;
	}
	public void setProductMap(ArrayList<String> matrixRow) {
		String product = matrixRow.get(0);
		Integer[] values = new Integer[matrixRow.size() - 1];
		
		for (int i = 1; i < matrixRow.size(); i++) {
			values[i-1] = Integer.valueOf(matrixRow.get(i));
		}
		
		this.productMap.put(product, values);
	}
}
