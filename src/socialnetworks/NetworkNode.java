package socialnetworks;

import java.math.BigDecimal;

public class NetworkNode implements Comparable<NetworkNode> {

	public NetworkNode(String farmId, double weigh) {
		this.farmId = farmId;
		this.weigh = new BigDecimal(weigh);
	}
	
	public NetworkNode(String farmId, BigDecimal weigh) {
		this.farmId = farmId;
		this.weigh = weigh;
	}

	private String farmId;
	private BigDecimal weigh;

	public String getFarmId() {
		return farmId;
	}

	public BigDecimal getWeigh() {
		return weigh;
	}

	@Override
	public int compareTo(NetworkNode o) {
		return this.getWeigh().compareTo(o.getWeigh());
	}
}
