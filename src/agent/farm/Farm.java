package agent.farm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import product.Product;

public class Farm implements Member {

	private String farmId;
	private Member head;
	private Member spouse;
	private Member child;
	private double[] coordinates; 
	
	
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
	
}