package decision;

import java.util.List;

import product.Product;

public class DecisionResult {

	private String farmId;
	private List<Product> products;
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
