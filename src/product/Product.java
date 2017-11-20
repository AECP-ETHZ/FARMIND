package product;

public interface Product {

	String getName();
	
	void setName(String name);
	
	Category getCategory();
	
	boolean isNone();
	
	public interface Category{
		
	}
}
