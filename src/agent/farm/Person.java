package agent.farm;

import java.util.List;

import product.Product;
import reader.Preferences;

/** 
 * Person object to go inside farm object. Contains parameters such as age, preferences, memory, and current products.
 * @author kellerke
 *
 */
public class Person {

	private int age;
	private int education;
	private Preferences preferences;
	private int memory;
	private double entrepreneurship;
	private List<Product> products;
	
	public Person(int age, int education, int memory, double entrepreneurship, Preferences preferences2, List<Product> actions) {
		this.age = age;
		this.education = education;
		this.preferences = preferences2;
		this.memory = memory;
		this.setEntrepreneurship(entrepreneurship);
		this.setProducts(actions);
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

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> actions) {
		this.products = actions;
	}

}
