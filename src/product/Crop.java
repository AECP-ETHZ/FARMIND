package product;

/**
 * Implements generic product interface
 * @see Product
 * @author kellerke
 *
 */
public class Crop implements Product {

	private String name;
	private int ID;

	/**
	 * Plant product for the farm
	 * Check name in master list of crops before object creation
	 * @param ID
	 * @param name
	 */
	public Crop(int ID, String name) {
		this.name = name;
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
