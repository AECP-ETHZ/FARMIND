package activity;

/**
 * Activity object for use in farm 
 * @see Product
 * @author kellerke
 *
 */
public class Activity {

	private String name;
	private int ID;

	/**
	 * Plant product for the farm
	 * Check name in master list of activities before object creation
	 * @param ID
	 * @param name
	 */
	public Activity(int ID, String name) {
		this.name = name;
		this.setId(ID);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		ID = id;
	}

	public int getID() {
		return ID;
	}

}
