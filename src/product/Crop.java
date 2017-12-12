package product;

public class Crop implements Product {

	private String name;
	private int ID;

	public Crop(int ID, String name) {
		this.name = name;
		this.setID(ID);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

}
