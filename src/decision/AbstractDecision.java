package decision;

import java.util.ArrayList;
import java.util.List;

import agent.farm.Farm;
import agent.government.Government;
import calculator.Calculator;
import product.Product;
import socialnetworks.SocialNetworks;

public abstract class AbstractDecision implements Decision {

	private Product product;

	@Override
	public DecisionResult make(Farm farm, List<Farm> adjacentFarms, SocialNetworks socialNetworks, Government government,
			Calculator calculator) {
		DecisionResult decisionResult=new DecisionResult();
		List<Product> products=new ArrayList<Product>();
		
		decisionResult.setProducts(products);
		
        int age = farm.getAge();
        double satisfaction = calculator.getSatisfaction(product, adjacentFarms, socialNetworks, government);
        double aspiration = calculator.getAspiration(product, adjacentFarms, socialNetworks, government);
        double uncertainty = calculator.getUncertainty(product, adjacentFarms, socialNetworks, government);
        double tolerance = calculator.getTolerance(product, adjacentFarms, socialNetworks, government);
        
        if(age < 60) {
        	if (satisfaction > aspiration) {
        		if (uncertainty > tolerance) {
        			//Do-imitation;
        			
        		}
        		else {
        			//Do-repetition;
        		}
        	}
        	else {
        		if (uncertainty > tolerance) {
        			//Do-inquiring;
        			
        		}
        		else {
        			//Do-optimization;
        		}
        		
        	}
        	//decisionResult =;
		}
        
		return decisionResult;
	}

}
