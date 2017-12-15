package product;

/**
 * Implements generic product interface
 * @see Product
 * @author kellerke
 *
 */
public class Livestock implements Product {

	private String name;
	private int ID;

	/**
	 * Animal product for the farm
	 * Check name in master list of livestock before object creation
	 * @param ID
	 * @param name
	 */
	public Livestock(int ID, String name) {
		this.setName(name);
		this.setId(ID);
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setId(int id) {
		ID = id;
	}

	@Override
	public int getID() {
		return ID;
	}
	
}
