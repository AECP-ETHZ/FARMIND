package activity;

/**
 * Each agent has a set of possible activities that are a result of each consumat decision. 
 * These activities could be crop or livestock production or a specific farming technology (ie Maize production using xyz pesticide).
 * <br>
 * Each agent contains a list of possible activities where each item is an activity object with a name and associated ID value. 
 * 
 * @author kellerke
 *
 */
public class Activity {

	private String name;												       // name of the activity 
	private int ID;															   // ID of the activity 

	/**
	 * Activity object constructor. Always check against master list of activities before creation.
	 * @param ID of the specific activity
	 * @param name of the activity (cattle, pigs, etc)
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
