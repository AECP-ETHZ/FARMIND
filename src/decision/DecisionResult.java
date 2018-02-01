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
	private int strategy;
	private List<String> LP_Products;
	private double income;
	private List<String> productNames;
	
	public DecisionResult(List<String> productNames, String farmId, List<String> maxSet, Integer year, Parameters param, int i, List<String> minSet, double income) {
		setFarmId(farmId);
		setProducts(maxSet);
		setYear(year);
		setParam(param);
		setStrategy(i);
		setLP_Products(minSet);
		setIncome(income);
		setProductNames(productNames);
	}
	
	public void appendDecisionFile(String fileName) {
		String PATH = "./output";
		File directory = new File(PATH);
		if(!directory.exists()) {
			directory.mkdir();
		}
		
		File file = new File(String.format("./output/%s.csv", fileName));
		FileWriter fw = null;
		try {
			fw = new FileWriter(file,true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter writer = new PrintWriter(bw);
		
		if (file.length() == 0) {
			writer.println("year, name, alpha_plus, alpha_minus, lambda, phi_plus, phi_minus, a, b, k, strategy, fuzzy_action1, "
					+ "fuzzy_action2, fuzzy_action3, fuzzy_action4, fuzzy_action5, fuzzy_action6, lp_action1, lp_action2, lp_action3, income");
		}
		
		writer.print(String.format("%s,",this.year));
		writer.print(String.format("%s,",this.farmId));
		writer.print(String.format("%s,",this.param.getAlpha_plus()));
		writer.print(String.format("%s,",this.param.getAlpha_minus()));
		writer.print(String.format("%s,",this.param.getLambda()));
		writer.print(String.format("%s,",this.param.getPhi_plus() ));
		writer.print(String.format("%s,",this.param.getPhi_minus() ));
		writer.print(String.format("%s,",this.param.getA() ));
		writer.print(String.format("%s,",this.param.getB() ));
		writer.print(String.format("%s,",this.param.getK() ));
		writer.print(String.format("%s,",this.strategy) );
		
		for(int i = 0; i < this.products.size(); i++) {
			writer.print(String.format("%d,",  this.productNames.indexOf( this.products.get(i)) ) );
		}
		
		for(int i = 0; i < 6 - this.products.size(); i++) {
			writer.print("NA," );
		}
		
		for(int i = 0; i < this.LP_Products.size(); i++) {
			writer.print(String.format("%d,",  this.productNames.indexOf(this.LP_Products.get(i))) );
		}
		
		for(int i = 0; i < 3 - this.LP_Products.size(); i++) {
			writer.print("NA," );
		}
	    
		writer.print(String.format("%s,",this.income ) );
		
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

	public int getStrategy() {
		return strategy;
	}

	public void setStrategy(int i) {
		this.strategy = i;
	}

	public List<String> getLP_Products() {
		return LP_Products;
	}

	public void setLP_Products(List<String> lP_Products) {
		LP_Products = lP_Products;
	}

	public double getIncome() {
		return income;
	}

	public void setIncome(double income) {
		this.income = income;
	}

	public List<String> getProductNames() {
		return productNames;
	}

	public void setProductNames(List<String> productNames) {
		this.productNames = productNames;
	}

}
