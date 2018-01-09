package agent.farm;

import java.util.List;

import product.Product;
import reader.Preferences;

public interface Member {
	
	public enum ACTION {
		REPETITION,
		OPTIMIZATION,
		IMITATION,
		OPT_OUT,
		EXIT,
	}

	int getAge();
	
	int getEducation();
	
	List<Product> getAction(List<Farm> farms);
	
	Preferences getPreferences();
	
	int getMemory();
}
