package agent;

public class Agent {
	
	private int Age;
	private int EducationLevel;
	
	public enum ACTION {
		REPETITION,
		OPTIMIZATION,
		IMITATION,
		OPT_OUT,
	}
	
	public Agent(int age, int education) {
		this.Age = age;
		this.EducationLevel = education;
	}
	
	public ACTION agentAction() {
		
		if ((Age <= 35)&&(EducationLevel > 10 )) {
			return ACTION.OPT_OUT;
		}
		if ((Age > 35)&&(EducationLevel > 10 )) {
			return ACTION.OPTIMIZATION;
		}
		if ((Age <= 35)&&(EducationLevel <= 10 )) {
			return ACTION.IMITATION;
		}
		if ((Age > 35)&&(EducationLevel <= 10 )) {
			return ACTION.REPETITION;
		}
		else {
			return null;
			}
	}
}
