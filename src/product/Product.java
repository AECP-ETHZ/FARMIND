package product;

/**
 *
 * A product is an action or crop/livestock associated with a farm.
 * Each farm will contain a list of preferences for action to undertake
 * 
 * @author kellerke
 */
public interface Product {

	String getName();
	
	void setName(String name);
	
	int getID();
	
	/**
	 * @param id contains information about category, subclass, and group of the product
	 */
	void setId(int id);
}
