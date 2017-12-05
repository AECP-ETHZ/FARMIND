package reader;

import java.util.List;

import agent.farm.Farm;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public interface Reader {
	
	List<Graph<String, DefaultEdge>> getSocialNetworks();
	
	List<Farm> getFarms();
}
