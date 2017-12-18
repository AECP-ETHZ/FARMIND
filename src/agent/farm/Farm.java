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
	private Person head;
	private Product CurrentAction;
	private Person spouse;
	private Person child;
	private Location location; 
	private Graph<String, DefaultEdge> network; 
	private double Satisfaction;
	private double Aspiration;
	private double Uncertainty;
	private double Tolerance;


	/** 
	 * Calculate satisfaction and uncertainty for the decision tree
	 * @return List of Products/Actions that the farm will produce
	 */
	public List<Product> getAction(List<Farm> farms) {
		
		// update satisfaction and uncertainty before making decisions
		updateSatisfaction();
		updateUncertainty(farms);
		
		// create final action array
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
	
	/**
	 * Update farm uncertainty
	 */
	private void updateUncertainty(List<Farm> farms) {
        double uncertainty = 0;
		int EdgeCount = 0;
		int totalFarms = 0;
        Set<DefaultEdge> E;
        Iterator<DefaultEdge> I;
    		
		E = this.network.outgoingEdgesOf(this.farmName);
		
		Object[] neighbors = this.network.vertexSet().toArray();
		
        I = E.iterator();
        EdgeCount = E.size();
        totalFarms = farms.size();
        
        for (int i = 0; i<= EdgeCount; i++)									// loop through all neighbors in the graph			
        {
        	for (int j = 0; j < totalFarms; j++) 
        	{
        		if (farms.get(j).farmName.equals(neighbors[i].toString() ) && !farms.get(j).farmName.equals(this.farmName)) {
        			System.out.println(neighbors[i].toString());
        			
        			List<Product> p = farms.get(j).head.getPreferences();
        			System.out.println( p );
        			System.out.println();
        		}
        	}
        }
        
        while (I.hasNext())
        {
        	uncertainty = uncertainty + this.network.getEdgeWeight(I.next());
        }
        		
		setUncertainty(uncertainty);
	}

	/**
	 * Update farm satisfaction level
	 */
	private void updateSatisfaction() {
		// setSatisfaction();
	}

	/**
	 * Using social network graph to calculate social weight of neighbors
	 * @param farms
	 * @return social weight of ties
	 */
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
	
	public void setTolerance(double entrepreneurship) {
		Tolerance = entrepreneurship;
	}
	
	public void setUncertainty(double uncertainty2) {
		Uncertainty = uncertainty2;
	}

	public void setAspiration(double aspiration) {
		Aspiration = aspiration;
	}

	public void setSatisfaction(double satisfaction) {
		Satisfaction = satisfaction;
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
	public Person getHead() {
		return head;
	}
	public void setHead(Person farmHead) {
		this.head = farmHead;
	}
	public Person getSpouse() {
		return spouse;
	}
	public void setSpouse(Person spouse) {
		this.spouse = spouse;
	}
	public Person getChild() {
		return child;
	}
	public void setChild(Person child) {
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

	public double getTolerance() {
		return Tolerance;
	}

	public double getUncertainty() {
		return Uncertainty;
	}

	public double getAspiration() {
		return Aspiration;
	}

	public double getSatisfaction() {
		return Satisfaction;
	}

}
