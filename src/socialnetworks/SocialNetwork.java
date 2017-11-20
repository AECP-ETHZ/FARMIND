package socialnetworks;

import java.util.ArrayList;
import java.util.List;

public class SocialNetwork {

	public SocialNetwork(String farmId) {
		this.framId = farmId;
		this.networkNodes = new ArrayList<NetworkNode>();
	}

	private String framId;

	private List<NetworkNode> networkNodes;

	public String getFramId() {
		return framId;
	}

	public void addNetworkNode(NetworkNode networkNode) {
		if (networkNode != null) {
			for (NetworkNode node : this.networkNodes) {
				if (node.getFarmId().equalsIgnoreCase(networkNode.getFarmId())) {
					return;
				}
			}
			this.networkNodes.add(networkNode);
		}
	}

	public List<NetworkNode> getNetworkNodes() {
		return this.networkNodes;
	}

	public double getWeigh(String correlativeFarmId) {
		if (correlativeFarmId != null && this.networkNodes != null) {
			for (NetworkNode networkNode : this.networkNodes) {
				if (networkNode.getFarmId().equalsIgnoreCase(correlativeFarmId)) {
					return networkNode.getWeigh().doubleValue();
				}
			}
		}
		return 0.00;
	}
}
