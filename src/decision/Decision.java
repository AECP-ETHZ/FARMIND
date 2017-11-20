package decision;

import java.util.List;

import agent.farm.Farm;
import agent.government.Government;
import calculator.Calculator;
import socialnetworks.SocialNetworks;

public interface Decision {

	DecisionResult make(Farm farm, List<Farm> adjacentFarms, SocialNetworks socialNetworks, Government government,
			Calculator calculator);
}
