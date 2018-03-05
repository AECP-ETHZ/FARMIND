package reader;

/**
 * For sensitivity testing, load various parameters for functions and models in the system.
 * Each farm will share the global parameters. 
 * @author kellerke
 *
 */
public class Parameters {
	
	private double alpha_plus;												   // used in satisfaction calc
	private double alpha_minus;										           // used in satisfaction calc
	private double lambda;													   // used in satisfaction calc
	private double phi_plus;												   // used in satisfaction calc
	private double phi_minus;												   // used in satisfaction calc
	private double a;														   // used in aspiration calc
	private double k;														   // scale factor for experience vector
	private double b;														   // used to set tolerance for dissimilarity
	private double m;														   // used to set tolerance for income changes
	private String name;													   // name of farm
	
	public double getAlpha_plus() {
		return alpha_plus;
	}
	public void setAlpha_plus(double alpha_plus) {
		this.alpha_plus = alpha_plus;
	}
	public double getAlpha_minus() {
		return alpha_minus;
	}
	public void setAlpha_minus(double alpha_minus) {
		this.alpha_minus = alpha_minus;
	}
	public double getLambda() {
		return lambda;
	}
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
	public double getPhi_plus() {
		return phi_plus;
	}
	public void setPhi_plus(double phi_plus) {
		this.phi_plus = phi_plus;
	}
	public double getPhi_minus() {
		return phi_minus;
	}
	public void setPhi_minus(double phi_minus) {
		this.phi_minus = phi_minus;
	}
	public double getA() {
		return a;
	}
	public void setA(double a) {
		this.a = a;
	}
	public double getK() {
		return k;
	}
	public void setK(double k) {
		this.k = k;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getB() {
		return b;
	}
	public void setB(double b) {
		this.b = b;
	}
	public double getM() {
		return m;
	}
	public void setM(double m) {
		this.m = m;
	}
}
