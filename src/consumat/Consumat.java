package consumat;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import agent.farm.Farm;
import output.BatchOutput;
import reader.ReadParameters;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public class Consumat {

	public static void main(String[] args) {
		
		
        Graph<String, DefaultEdge> g = new SimpleWeightedGraph<String, DefaultEdge>(DefaultEdge.class);
        int EdgeCount;
        double sum = 0;

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";
        String v5 = "v5";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);

        // add edges to create a star graph
        DefaultEdge e = g.addEdge(v1, v2);
        g.setEdgeWeight(e,30.0);
       
        e = g.addEdge(v1, v3);
        g.setEdgeWeight(e,20.0);
        
        e = g.addEdge(v1, v4);
        g.setEdgeWeight(e,15.0);
        
        e = g.addEdge(v1, v5);
        g.setEdgeWeight(e,22.0);
        
        System.out.println(g.toString());
        
        Set<DefaultEdge> E = g.outgoingEdgesOf(v1);
        Iterator<DefaultEdge> I = E.iterator();
        
        EdgeCount = E.size();
        
        while (I.hasNext())
        {
        	sum = sum + g.getEdgeWeight(I.next());
        }
        
        double avg = sum/EdgeCount;
        System.out.println(avg);
		

		// 1 input parameters
		ReadParameters reader = new ReadParameters();
		
		// 2 create agents
		List<Farm> farms = reader.getFarms();

		// 3 decision making
		System.out.println(String.format("Action: %s", farms.get(0).getAction() ));	
		System.out.println(String.format("Action: %s", farms.get(1).getAction() ));	
		System.out.println(String.format("Action: %s", farms.get(2).getAction() ));	
		
		// 4 output batch file
		BatchOutput batch = new BatchOutput();
		batch.write();
	}
}
