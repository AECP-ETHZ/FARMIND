package socialnetworks;

import java.math.BigDecimal;

public class NetworkNode implements Comparable<NetworkNode> {

	public NetworkNode(String farmId, double weight) {
		this.farmId = farmId;
		this.weight = new BigDecimal(weight);
	}
	
	public NetworkNode(String farmId, BigDecimal weight) {
		this.farmId = farmId;
		this.weight = weight;
	}

	private String farmId;
	private BigDecimal weight;

	public String getFarmId() {
		return farmId;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	@Override
	public int compareTo(NetworkNode o) {
		return this.getWeight().compareTo(o.getWeight());
	}
}
