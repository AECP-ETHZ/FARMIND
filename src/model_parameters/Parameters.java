package model_parameters;

public class Parameters {
	
	private int sim_year = 0;
	private String modelName;
	private boolean uncertainty;
	
	public Parameters(String[] args) {
		this.setSim_year( Integer.parseInt(args[0]));
		this.setModelName(args[1]);
		this.setUncertainty(Boolean.parseBoolean(args[2]));
	}

	public int getSim_year() {
		return sim_year;
	}

	public void setSim_year(int sim_year) {
		this.sim_year = sim_year;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public boolean isUncertainty() {
		return uncertainty;
	}

	public void setUncertainty(boolean uncertainty) {
		this.uncertainty = uncertainty;
	}

}
