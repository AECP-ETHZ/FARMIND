package consumat;

import java.io.FileNotFoundException;
import java.util.List;

import agent.farm.DefaultMember;
import agent.farm.Farm;
import agent.government.Government;
import calculator.Calculator;
import calculator.MockCalculator;
import decision.Decision;
import decision.DecisionResult;
import decision.AbstractDecision;
import output.Output;
import product.Crop;
import product.Livestock;
import product.Product;
import reader.MockReader;
import reader.ParameterInput;
import reader.Reader;
import socialnetworks.SocialNetworks;

import agent.Agent;

public class Consumat {

	public static void main(String[] args) {
		/*
		// reader
		Reader reader = new MockReader();
		// read social networks
		SocialNetworks socialNetworks = reader.getSocalNetwroks();
		System.out.println(socialNetworks.toString());
		System.out.println(String.format("Farm001 -> Farm003=%s", socialNetworks.getWeigh("Farm001", "Farm003")));
		// read farms info
		List<Farm> farms = reader.getFarms();
		System.out.println(
				String.format("farm -> farmId:[%s], age:[%s], education=[%s], memory=[%s] \n", farms.get(0).getFarmId(),
						farms.get(0).getAge(), farms.get(0).getEducation(), farms.get(0).getMemory()));
		// test calculator
		Product potatoes = new Crop("potatoes");
		Product diaryCattle = new Livestock("diary cattle");
		Government government = new Government();
		Calculator calculator = new MockCalculator();
		calculator.getSatisfaction(potatoes, farms, socialNetworks, government);
		calculator.getSatisfaction(diaryCattle, farms, socialNetworks, government);
		*/
		
		// 1 read parameters
		ParameterInput param = new ParameterInput();
		try {
			param.readParameters();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		// 2 create agent
		DefaultMember farmer = new DefaultMember(50, 10, 5);

		// 3 decision making
		System.out.println(String.format("Action: %s", farmer.agentAction()));	
		
		// 4 output batch file
		BatchOutput batch = new BatchOutput();
		try {
			batch.generateBatch();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
