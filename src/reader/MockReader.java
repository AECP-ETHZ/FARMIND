package reader;

import java.util.ArrayList;
import java.util.List;

import agent.farm.DefaultMember;
import agent.farm.Farm;
import product.Crop;
import product.Livestock;
import product.Product;
import socialnetworks.NetworkNode;
import socialnetworks.SocialNetwork;
import socialnetworks.SocialNetworks;

public class MockReader implements Reader {

	@Override
	public SocialNetworks getSocalNetwroks() {
		SocialNetworks socialNetworks = new SocialNetworks();
		// read social networks from input file
		SocialNetwork socialNetwork = new SocialNetwork("Farm001");
		socialNetwork.addNetworkNode(new NetworkNode("Farm002", 0.3));
		socialNetwork.addNetworkNode(new NetworkNode("Farm003", 0.2));
		socialNetwork.addNetworkNode(new NetworkNode("Farm003", 0.5));

		socialNetworks.addSocialNetwork(socialNetwork);
		return socialNetworks;
	}

	@Override
	public List<Farm> getFarms() {
		List<Farm> farms = new ArrayList<Farm>();
		// farm001
		Farm farm001 = new Farm();
		farm001.setFarmId("Farm001");
		// farm001's head
		DefaultMember farm001Head = new DefaultMember();
		farm001Head.setAge(34);
		farm001Head.setEducation(19);
		farm001Head.setMemory(5);
		// farm001Head's preferences
		List<Product> farm001HeadPreferences = new ArrayList<Product>();
		Product maize = new Crop("maize");
		Product diaryCattle = new Livestock("diary cattle");
		farm001HeadPreferences.add(maize);
		farm001HeadPreferences.add(diaryCattle);
		farm001Head.setPreferences(farm001HeadPreferences);
		farm001.setHead(farm001Head);

		// farm001's spouse
		DefaultMember farm001Spouse = new DefaultMember();
		farm001Spouse.setAge(32);
		farm001Spouse.setEducation(14);
		farm001Spouse.setMemory(6);
		// farm001Spouse's preferences
		List<Product> farm001SpousePreferences = new ArrayList<Product>();
		Product wheat = new Crop("wheat");
		Product pigs = new Livestock("pigs");
		farm001SpousePreferences.add(wheat);
		farm001SpousePreferences.add(pigs);
		farm001Spouse.setPreferences(farm001HeadPreferences);
		farm001.setSpouse(farm001Spouse);
		

		// farm001's child
		DefaultMember farm001Child = new DefaultMember();
		farm001Child.setAge(9);
		farm001Child.setEducation(4);
		farm001Child.setMemory(2);
		// farm001Child's preferences
		List<Product> farm001ChildPreferences = new ArrayList<Product>();
		farm001ChildPreferences.add(wheat);
		farm001ChildPreferences.add(maize);
		farm001Child.setPreferences(farm001ChildPreferences);
		farm001.setChild(farm001Child);

		farms.add(farm001);
		return farms;
	}

}
