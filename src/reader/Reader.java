package reader;

import java.util.List;

import agent.farm.Farm;

public interface Reader {
	
	/**
	 * Each farm in the list contains a social network, the associated people, and prefered crops
	 * The satisfaction and uncertainty are generated initially
	 * @return List of all farm objects from the input csv file
	 */
	List<Farm> getFarms();
}
