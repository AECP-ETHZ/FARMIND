package agent.farm;

import java.util.List;

import product.Product;

public class Person {

	private int age;
	private int education;
	private List<Product> preferences;
	private int memory;
	private double entrepreneurship;
	
	public Person(int age, int education, int memory, double entrepreneurship, List<Product> preferences) {
		this.age = age;
		this.education = education;
		this.preferences = preferences;
		this.memory = memory;
		this.setEntrepreneurship(entrepreneurship);
	}
	

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getEducation() {
		return education;
	}

	public void setEducation(int education) {
		this.education = education;
	}

	public List<Product> getPreferences() {
		return preferences;
	}

	public void setPreferences(List<Product> preferences) {
		this.preferences = preferences;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public double getEntrepreneurship() {
		return entrepreneurship;
	}

	public void setEntrepreneurship(double entrepreneurship2) {
		this.entrepreneurship = entrepreneurship2;
	}

}
