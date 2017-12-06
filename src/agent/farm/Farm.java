package agent.farm;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import product.Product;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class Farm implements Member {
	
	private String farmId;
	private String farmName;
	private Member head;
	private Member spouse;
	private Member child;
	private Location location; 
	private Graph<String, DefaultEdge> network; 

	@Override
	public int getAge() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getEducation() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List<Product> getPreferences() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getMemory() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public agent.farm.Person.ACTION getAction() {
		
		if ((head.getAge() <= 35)&&(head.getEducation() > 10 )) {
			return ACTION.OPT_OUT;
		}
		if ((head.getAge() > 35)&&(head.getEducation() > 10 )) {
			return ACTION.OPTIMIZATION;
		}
		if ((head.getAge() <= 35)&&(head.getEducation() <= 10 )) {
			return ACTION.IMITATION;
		}
		if ((head.getAge() > 35)&&(head.getEducation() <= 10 )) {
			return ACTION.REPETITION;
		}
		else {
			return null;
			}
	}
	
	public double getSocialTies() {
        double sum = 0;
        double avg = 0;
		int EdgeCount;
        Set<DefaultEdge> E;
        Iterator<DefaultEdge> I;
    		
		E = this.network.outgoingEdgesOf(this.farmName);
        I = E.iterator();
        
        EdgeCount = E.size();
        while (I.hasNext())
        {
        	sum = sum + this.network.getEdgeWeight(I.next());
        }
        
        avg = sum/EdgeCount;
		return avg;
		
	}
	
	public String getFarmId() {
		return farmId;
	}
	public void setFarmId(String farmId) {
		this.farmId = farmId;
	}
	public Member getHead() {
		return head;
	}
	public void setHead(Member head) {
		this.head = head;
	}
	public Member getSpouse() {
		return spouse;
	}
	public void setSpouse(Member spouse) {
		this.spouse = spouse;
	}
	public Member getChild() {
		return child;
	}
	public void setChild(Member child) {
		this.child = child;
	}
	public String getFarmName() {
		return farmName;
	}
	public void setFarmName(String farmName) {
		this.farmName = farmName;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Graph<String, DefaultEdge> getNetwork() {
		return network;
	}
	public void setNetwork(Graph<String, DefaultEdge> network) {
		this.network = network;
	}

}
