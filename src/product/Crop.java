package product;

public class Crop extends Order implements Product {

	public Crop(String name) {
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
	public Category getCategory() {
		return CropCategory.toCategory(this.name);
	}

	@Override
	public boolean isNone() {
		return this.getCategory() == CropCategory.None;
	}

	public enum CropCategory implements Product.Category {
		None("none"), Potatoes("potatoes"), Wheat("wheat"), Rice("rice"), Maize("maize"), Carrots("carrots"),Eggplants("eggplants"),Lettuce("lettuce");

		private String name;

		private CropCategory(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static CropCategory toCategory(String name) {
			for (CropCategory cropCategory : values()) {
				if (cropCategory.getName().equalsIgnoreCase(name)) {
					return cropCategory;
				}
			}
			return CropCategory.None;
		}
	}

}
