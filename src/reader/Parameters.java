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
	private double b;														   // used to set tolerance for dissimilarity
	private double m;														   // used to set tolerance for income changes
	private double beta_s;													   // used to set percentage of criteria in fuzzy selection
	private double beta_q;													   // used to set percentage of criteria in fuzzy selection
	private String name;													   // name of parameter set
	
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
	public double getBeta_s() {
		return beta_s;
	}
	public void setBeta_s(double beta_s) {
		this.beta_s = beta_s;
	}
	public double getBeta_q() {
		return beta_q;
	}
	public void setBeta_q(double beta_q) {
		this.beta_q = beta_q;
	}
}
