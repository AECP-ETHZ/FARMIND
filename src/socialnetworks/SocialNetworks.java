package socialnetworks;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class SocialNetworks {

	public SocialNetworks() {
		this.socialNetworks = new ArrayList<SocialNetwork>();
	}

	private List<SocialNetwork> socialNetworks;

	private SocialNetwork getSocialNetwork(String farmId) {
		if (farmId != null) {
			for (SocialNetwork socialNetwork : this.socialNetworks) {
				if (socialNetwork.getFramId().equalsIgnoreCase(farmId)) {
					return socialNetwork;
				}
			}
		}
		return null;
	}

	public void addSocialNetwork(SocialNetwork socialNetwork) {
		if (socialNetwork != null) {
			socialNetwork.addNetworkNode(new NetworkNode(socialNetwork.getFramId(), 0.00));
			this.socialNetworks.add(socialNetwork);
			for (NetworkNode networkNode : socialNetwork.getNetworkNodes()) {
				SocialNetwork other = this.getSocialNetwork(networkNode.getFarmId());
				if (other == null) {
					other = new SocialNetwork(networkNode.getFarmId());
					this.socialNetworks.add(other);
				}
				other.addNetworkNode(new NetworkNode(socialNetwork.getFramId(), networkNode.getWeigh()));
			}
		}
	}

	public double getWeigh(String farmId, String correlativeFarmId) {
		SocialNetwork socialNetwork = this.getSocialNetwork(farmId);
		if (socialNetwork != null) {
			return socialNetwork.getWeigh(correlativeFarmId);
		}
		return 0.00;
	}

	public List<NetworkNode> getNetworkNodes(String farmId) {
		SocialNetwork socialNetwork = this.getSocialNetwork(farmId);
		if (socialNetwork != null) {
			return socialNetwork.getNetworkNodes();
		}
		return new ArrayList<NetworkNode>();
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		for (SocialNetwork socialNetwork : this.socialNetworks) {
			for (NetworkNode networkNode : socialNetwork.getNetworkNodes()) {
				stringBuffer.append(String.format("[%s,%s] -> %s\t", socialNetwork.getFramId(), networkNode.getFarmId(),
						networkNode.getWeigh().setScale(2,RoundingMode.HALF_UP).doubleValue()));
			}
			stringBuffer.append("\n");
		}
		return stringBuffer.toString();
	}
}
