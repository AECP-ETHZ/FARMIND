package agent.farm;

import java.util.ArrayList;
import java.util.List;

import product.Product;

public class Person implements Member {

	private int age;
	private int education;
	private List<Product> preferences;
	private int memory;
	
	public Person(int age, int education, int memory) {
		this.age = age;
		this.education = education;
		this.preferences = new ArrayList<Product>();
		this.memory = memory;
	}
	
	@Override
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public int getEducation() {
		return education;
	}

	public void setEducation(int education) {
		this.education = education;
	}

	@Override
	public List<Product> getPreferences() {
		return preferences;
	}

	public void setPreferences(List<Product> preferences) {
		this.preferences = preferences;
	}

	@Override
	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	@Override
	public ACTION getAction() {
		// TODO Auto-generated method stub
		return null;
	}

}
