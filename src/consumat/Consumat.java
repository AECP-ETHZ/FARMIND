package consumat;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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
import reader.MockReader1;
import reader.ParameterInput;
import reader.Reader;
import socialnetworks.SocialNetworks;

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
		
		// set up loop around reading/output cycle
		
		// 1 read parameters
		ParameterInput param = new ParameterInput();
		ArrayList<Integer> preferences = new ArrayList<Integer>();
		
		try {
			preferences = param.readParameters();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		MockReader1 reader = new MockReader1();
		List<Farm> farms = reader.getFarms();
		
		// 2 create agent
		int age = preferences.get(0);
		int education = preferences.get(1);
		int memory = preferences.get(2);
		DefaultMember farmer = new DefaultMember(age, education, memory);

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
