package calculator;

import java.util.List;

import agent.farm.Farm;
import agent.government.Government;
import product.Crop;
import product.Livestock;
import product.Product;
import socialnetworks.SocialNetworks;
import product.Crop.CropCategory;
import product.Livestock.LivestockCategory;

public class DefaultCalculator implements Calculator {

	@Override
	public double getSatisfaction(Product product, List<Farm> adjacentFarms, SocialNetworks socialNetworks,
			Government government) {
		if (product.isNone()) {
			System.out.println("do nothing, the satisfaction is 0");
		} else {
			if (product instanceof Crop) {
				// crop
				if (product.getCategory() == CropCategory.Potatoes) {
					System.out.println("I'm a potato, the satisfaction is 5");
				}
			} else if (product instanceof Livestock) {
				// livestock
				if (product.getCategory() == LivestockCategory.Diary_Cattle) {
					System.out.println("I'm a diary cattle, the satisfaction is 7");
				}
			}
		}
		return 0;
	}

	@Override
	public double getAspiration(Product product, List<Farm> adjacentFarms, SocialNetworks socialNetworks,
			Government government) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getUncertainty(Product product, List<Farm> adjacentFarms, SocialNetworks socialNetworks,
			Government government) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTolerance(Product product, List<Farm> adjacentFarms, SocialNetworks socialNetworks,
			Government government) {
		// TODO Auto-generated method stub
		return 0;
	}

}
