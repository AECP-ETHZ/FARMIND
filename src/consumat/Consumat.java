package consumat;

import java.util.List;

import agent.farm.Farm;
import agent.government.Government;
import calculator.Calculator;
import calculator.DefaultCalculator;
import decision.Decision;
import decision.DecisionResult;
import decision.DefaultDecision;
import output.Output;
import output.TextOutput;
import product.Crop;
import product.Livestock;
import product.Product;
import reader.MockReader;
import reader.Reader;
import socialnetworks.SocialNetworks;

public class Consumat {

	public static void main(String[] args) {
		// reader
		Reader reader = new MockReader();
		// read social networks
		SocialNetworks socialNetworks = reader.getSocalNetwroks();
		System.out.println(socialNetworks.toString());
		System.out.println(String.format("Farm001->Farm003=%s", socialNetworks.getWeigh("Farm001", "Farm003")));
		// read farms info
		List<Farm> farms = reader.getFarms();
		System.out.println(
				String.format("farm->farmId:[%s], age:[%s],education=[%s],memory=[%s]", farms.get(0).getFarmId(),
						farms.get(0).getAge(), farms.get(0).getEducation(), farms.get(0).getMemory()));
		// test calculator
		Product potatoes = new Crop("potatoes");
		Product diaryCattle = new Livestock("diary cattle");
		Government government = new Government();
		Calculator calculator = new DefaultCalculator();
		calculator.getSatisfaction(potatoes, farms, socialNetworks, government);
		calculator.getSatisfaction(diaryCattle, farms, socialNetworks, government);

		// decision-making
		Decision decision = new DefaultDecision();
		DecisionResult decisionResult = decision.make(farms.get(0), farms, socialNetworks, government, calculator);
		// output the decision result
		Output output = new TextOutput();
		output.write(decisionResult);

	}

}
