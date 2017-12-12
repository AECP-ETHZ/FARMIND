package agent.farm;

import java.util.ArrayList;
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
	private Product CurrentAction;
	private Member spouse;
	private Member child;
	private Location location; 
	private Graph<String, DefaultEdge> network; 
	
	private int Satisfaction;
	private int Aspiration;
	private int Uncertainty;
	private int Tolerance;

	public void setTolerance(int tolerance) {
		Tolerance = tolerance;
	}
	
	public void setUncertainty(int uncertainty) {
		Uncertainty = uncertainty;
	}

	public void setAspiration(int aspiration) {
		Aspiration = aspiration;
	}

	public void setSatisfaction(int satisfaction) {
		Satisfaction = satisfaction;
	}
	
	public List<Product> getAction() {
		
		List<Product> products = new ArrayList<Product>();
		products.add(this.CurrentAction);
		
		if ((head.getAge() > 65)) {
			System.out.println(ACTION.EXIT);
			products.add(this.CurrentAction);
		}
		else if (this.Uncertainty >= this.Tolerance) {
			if (this.Satisfaction > this.Aspiration) {
				System.out.println(ACTION.IMITATION);
			}
			else {
				System.out.println(ACTION.OPT_OUT);
			}
		}
		else {
			if (this.Satisfaction > this.Aspiration) {
				System.out.println(ACTION.REPETITION);
			}
			else {
				System.out.println(ACTION.OPTIMIZATION);
			}
		}
		return products;
	}
	
	public double getSocialTies(List<Farm> farms) {
        double sum = 0;
        double avg = 0;
		int EdgeCount;
        Set<DefaultEdge> E;
        Iterator<DefaultEdge> I;
    		
		E = this.network.outgoingEdgesOf(this.farmName);
		
		Object[] x = this.network.vertexSet().toArray();
		
        I = E.iterator();
        
        EdgeCount = E.size();
        
        for (int i = 0; i<= E.size(); i++)
        {
        	
        	for (int j = 0; j<farms.size();j++) {
        	
        		if (farms.get(j).farmName.equals(x[i].toString() ) && !farms.get(j).farmName.equals(this.farmName)) {
        			System.out.println(x[i].toString());
        			
        			//List<Product> p = farms.get(j).head.getPreferences();
        			//System.out.println( p );
        		}
        	}
        }
        
        while (I.hasNext())
        {
        	sum = sum + this.network.getEdgeWeight(I.next());
        }
        
        avg = sum/EdgeCount;
		return avg;
		
	}
	
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
	public Product getCurrentAction() {
		return CurrentAction;
	}
	public void setCurrentAction(Product current_action) {
		CurrentAction = current_action;
	}

	public int getTolerance() {
		return Tolerance;
	}

	public int getUncertainty() {
		return Uncertainty;
	}

	public int getAspiration() {
		return Aspiration;
	}

	public int getSatisfaction() {
		return Satisfaction;
	}

}
