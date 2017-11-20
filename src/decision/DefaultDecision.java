package decision;

import java.util.ArrayList;
import java.util.List;

import agent.farm.Farm;
import agent.government.Government;
import calculator.Calculator;
import product.Product;
import socialnetworks.SocialNetworks;

public class DefaultDecision implements Decision {

	@Override
	public DecisionResult make(Farm farm, List<Farm> adjacentFarms, SocialNetworks socialNetworks, Government government,
			Calculator calculator) {
		DecisionResult decisionResult=new DecisionResult();
		List<Product> products=new ArrayList<Product>();
		
		decisionResult.setProducts(products);
		return decisionResult;
	}

}
