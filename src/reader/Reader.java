package reader;

import java.util.List;

import agent.farm.Farm;
import socialnetworks.SocialNetworks;

public interface Reader {
	
	SocialNetworks getSocalNetwroks();
	
	List<Farm> getFarms();
}
