package decision;

import java.util.List;

import product.Product;

/**
 * Decision object that stores decision
 * @author kellerke
 *
 */
public class DecisionResult {

	private String farmId;
	private List<Product> products;
	
	public DecisionResult(String farmId, List<Product> p) {
		setFarmId(farmId);
		setProducts(p);
	}
	public String getFarmId() {
		return farmId;
	}
	public void setFarmId(String farmId) {
		this.farmId = farmId;
	}
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
