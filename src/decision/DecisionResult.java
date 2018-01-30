package decision;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import reader.Parameters;

/**
 * Decision object with unique information for each farm 
 * Can be used to append to the final output list
 * @author kellerke
 *
 */
public class DecisionResult {

	private String farmId;
	private List<String> products;
	private Integer year;
	private Parameters param;
	private String strategy;
	
	public DecisionResult(String farmId, List<String> p, Integer year, Parameters param, String strategy) {
		setFarmId(farmId);
		setProducts(p);
		setYear(year);
		setParam(param);
		setStrategy(strategy);
	}
	
	public void appendDecisionFile() {
		File file =new File("./BatchFiles/out.txt");
		FileWriter fw = null;
		try {
			fw = new FileWriter(file,true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter writer = new PrintWriter(bw);
		 
		writer.print(String.format("%s, ",this.year));
		writer.print(String.format("%s, ",this.farmId));
		writer.print(String.format("%s, ",this.param.getAlpha_plus()));
		writer.print(String.format("%s, ",this.param.getAlpha_minus()));
		writer.print(String.format("%s, ",this.param.getLambda()));
		writer.print(String.format("%s, ",this.param.getPhi_plus() ));
		writer.print(String.format("%s, ",this.param.getPhi_minus() ));
		writer.print(String.format("%s, ",this.param.getK() ));
		writer.print(String.format("%s, ",this.strategy) );
		
		for(int i = 0; i < this.products.size(); i++) {
			writer.print(String.format("%s, ",this.products.get(i)) );
		}
		
		writer.println();
		writer.close();
	}
	
	public String getFarmId() {
		return farmId;
	}
	public void setFarmId(String farmId) {
		this.farmId = farmId;
	}
	public List<String> getProducts() {
		return products;
	}
	public void setProducts(List<String> products) {
		this.products = products;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Parameters getParam() {
		return param;
	}

	public void setParam(Parameters param) {
		this.param = param;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

}
