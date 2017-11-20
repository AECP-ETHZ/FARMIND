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

	@Override
	public int getAge() {
		if (this.head != null)
			return this.head.getAge();
		return 0;
	}

	@Override
	public int getEducation() {
		if (this.head != null)
			return this.head.getEducation();
		return 0;
	}

	@Override
	public List<Product> getPreferences() {
		if (this.head != null)
			return Collections.unmodifiableList(this.head.getPreferences());
		return new ArrayList<Product>();
	}

	@Override
	public int getMemory() {
		if (this.head != null)
			return this.head.getMemory();
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
