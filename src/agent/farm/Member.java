package agent.farm;

import java.util.List;

import product.Product;

public interface Member {

	int getAge();
	
	int getEducation();
	
	List<Product> getPreferences();
	
	int getMemory();
}
