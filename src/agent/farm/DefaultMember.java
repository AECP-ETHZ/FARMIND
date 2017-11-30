package agent.farm;

import java.util.ArrayList;
import java.util.List;

import agent.Agent.ACTION;
import product.Product;

public class DefaultMember implements Member {

	public enum ACTION {
		REPETITION,
		OPTIMIZATION,
		IMITATION,
		OPT_OUT,
	}
	
	private int age;
	private int education;
	private List<Product> preferences;
	private int memory;
	
	public DefaultMember(int age, int education, int memory) {
		this.age = 0;
		this.education = 0;
		this.preferences = new ArrayList<Product>();
		this.memory = 0;
	}
	
	public ACTION agentAction() {
		
		if ((age <= 35)&&(education > 10 )) {
			return ACTION.OPT_OUT;
		}
		if ((age > 35)&&(education > 10 )) {
			return ACTION.OPTIMIZATION;
		}
		if ((age <= 35)&&(education <= 10 )) {
			return ACTION.IMITATION;
		}
		if ((age > 35)&&(education <= 10 )) {
			return ACTION.REPETITION;
		}
		else {
			return null;
			}
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

}
