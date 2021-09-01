package agent;

/** 
 * Person object contains parameters age, education and memory length of farm member.
 *
 */
public class Person {

    private int age;        // age of farmer
    private int education;  // schooling years of farmer
    private int memory;     // number of years that farmer can remember experiences or income
    
    public Person(int age, int education, int memory) {
        this.age = age;
        this.education = education;
        this.memory = memory;
    }
    
    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getEducation() {
        return this.education;
    }

    public void setEducation(int education) {
        this.education = education;
    }

    public int getMemory() {
        return this.memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }
}
