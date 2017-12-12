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
	
}
