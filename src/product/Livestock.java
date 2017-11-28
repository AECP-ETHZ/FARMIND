package product;

public class Livestock extends Order implements Product {

	public Livestock(String name) {
		this.name = name;
	}

	private String name;

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
		None("none"), Diary_Cattle("diary cattle"), Beef_Cattle("beef cattle"), Pigs("pigs"), Goats("goats"), Sheep(
				"sheep");

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
