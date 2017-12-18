package reader;

import java.util.List;

import agent.farm.Farm;

/**
 * Read the input parameter CSV files to create crop/livestock lists, farm social networks, and farm lists
 * @author kellerke
 *
 */
public interface Reader {
	
	/**
	 * Each farm in the list contains a social network, the associated people, and preferred crops
	 * The satisfaction and uncertainty are generated initially
	 * @return List of all farm objects from the input csv file
	 */
	List<Farm> getFarms();
}
