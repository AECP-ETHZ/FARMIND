package agent.farm;

import reader.Preferences;

public class Person {

	private int age;
	private int education;
	private Preferences preferences;
	private int memory;
	private double entrepreneurship;
	
	public Person(int age, int education, int memory, double entrepreneurship, Preferences preferences2) {
		this.age = age;
		this.education = education;
		this.preferences = preferences2;
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

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
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
