package product;

public class Livestock implements Product {

	private String name;
	private int ID;

	public Livestock(int ID, String name) {
		this.name = name;
		this.setID(ID);
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isNone() {
		return this.getCategory() == LivestockCategory.None;
	}

	@Override
	public Category getCategory() {
		return LivestockCategory.toCategory(this.name);
	}

	public enum LivestockCategory implements Product.Category {
		None("none"), Dairy_Cattle("dairy_cattle"), Beef_Cattle("beef_cattle"), Pigs("pigs"), Goats("goats"), Sheep(
				"sheep"),Ducks("ducks"),Geese("geese"),Chickens("chickens");

		private String name;

		private LivestockCategory(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static LivestockCategory toCategory(String name) {
			for (LivestockCategory livestockCategory : values()) {
				if (livestockCategory.getName().equalsIgnoreCase(name)) {
					return livestockCategory;
				}
			}
			return LivestockCategory.None;
		}
	}
}
