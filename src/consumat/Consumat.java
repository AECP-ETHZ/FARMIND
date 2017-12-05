package consumat;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import agent.farm.Farm;
import output.BatchOutput;
import reader.ReadParameters;

import org.jgrapht.Graph;
import org.jgrapht.graph.*;

public class Consumat {

	public static void main(String[] args) {
		
		
        Graph<String, DefaultEdge> g = new SimpleWeightedGraph<String, DefaultEdge>(DefaultEdge.class);
        int EdgeCount;
        double sum = 0;
        
        double avg = 0;
        Set<DefaultEdge> E;
        Iterator<DefaultEdge> I;
		
		// 1 input parameters
		ReadParameters reader = new ReadParameters();
		
		// 2 create agents
		List<Farm> farms = reader.getFarms();
		List<Graph<String, DefaultEdge>> network = reader.getSocialNetworks();
		
		g = network.get(2);
		
		System.out.println(g);
		
		E = g.outgoingEdgesOf("Farm_C");
        I = E.iterator();
        
        EdgeCount = E.size();
        
        while (I.hasNext())
        {
        	sum = sum + g.getEdgeWeight(I.next());
        }
        
        avg = sum/EdgeCount;
        System.out.println(avg);
		
		
		System.out.println();

		// 3 decision making
		System.out.println(String.format("Action: %s", farms.get(0).getAction() ));	
		System.out.println(String.format("Action: %s", farms.get(1).getAction() ));	
		System.out.println(String.format("Action: %s", farms.get(2).getAction() ));	
		
		// 4 output batch file
		BatchOutput batch = new BatchOutput();
		batch.write();
	}
}
