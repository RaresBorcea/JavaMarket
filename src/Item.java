import java.util.Locale;

public class Item {
	private String name;
	private int ID;
	private double price;
	
	Item(String name, int ID, double price) {
		this.name = name;
		this.ID = ID;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	// using java.util.Locale to respect output format
	public String toString() {
		String s = new String();
		String pr = String.format(Locale.US, "%.2f", price);
		s += name + ";" + ID + ";" + pr;
		return s;
	}
}
