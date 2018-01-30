package reader;

/**
 * For sensitivity testing, load various parameters for functions and models in the system.
 * Each farm will share the global parameters
 * @author kellerke
 *
 */
public class Parameters {
	
	private double alpha_plus;
	private double alpha_minus;
	private double lambda;
	private double phi_plus;
	private double phi_minus;
	private double a;
	private double k;
	private String name;
	
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
	
	


	

}
