package calculator;

import java.util.List;

import agent.farm.Farm;
import agent.government.Government;
import product.Product;
import socialnetworks.SocialNetworks;

public interface Calculator {

	double getSatisfaction(Product product, List<Farm> adjacentFarms, SocialNetworks socialNetworks,
			Government government);

	double getAspiration(Product product, List<Farm> adjacentFarms, SocialNetworks socialNetworks,
			Government government);

	double getUncertainty(Product product, List<Farm> adjacentFarms, SocialNetworks socialNetworks,
			Government government);

	double getTolerance(Product product, List<Farm> adjacentFarms, SocialNetworks socialNetworks,
			Government government);
}
