package calculator;

import java.util.List;

import agent.farm.Farm;
import agent.government.Government;
import product.Product;
import socialnetworks.SocialNetworks;

public class MockCalculator implements Calculator {

	@Override
	public double getSatisfaction(Product product, List<Farm> adjacentFarms, SocialNetworks socialNetworks,
			Government government) {
		/*
		if (product.isNone()) {
			System.out.println("No product. Do nothing.");
		} else {
			if (product instanceof Crop) {
				// Crop
				if (product.getCategory() == CropCategory.Potatoes) {
					System.out.println("test from MockCalculator: potato");
					Crop potatoes = (Crop)product;
					//potatoes.(see its characteristics)
				}
			} else if (product instanceof Livestock) {
				// Livestock
				if (product.getCategory() == LivestockCategory.Dairy_Cattle) {
					System.out.println("test from MockCalculator: diary cattle");
					Livestock diaryCattles = (Livestock)product;
					//diaryCattles.
				}
			}
		}
		*/
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
