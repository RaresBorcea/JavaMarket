import java.util.Vector;

public class Store {
	//using Singleton pattern for store creation
	private static Store instance = null;
	String name;
	Vector departments;
	Vector customers;
	
	private Store(String name) {
        this.name = name;
        this.departments = new Vector();
        this.customers = new Vector();
    }
	
	public static Store getInstance(String name) {
        if (instance == null)
        	instance = new Store(name); 
        return instance;
    }
	
	public void enter(Customer c) {
		if(!customers.contains(c)) {
			customers.add(c);
		}
	}
	
	public void exit(Customer c) {
		customers.remove(c);
	}
	
	public ShoppingCart getShoppingCart(Double budget) {
		return new ShoppingCart(new CompCartAndA(), budget);
	}
	
	public Vector getCustomers() {
		return customers;
	}
	
	public Vector getDepartments() {
		return departments;
	}
	
	public void addDepartment(Department d) {
		if(!departments.contains(d)) {
			departments.add(d);
		}
	}
	
	public Department getDepartment(Integer ID) {
		for(int i = 0; i < departments.size(); i++) {
			if(((Department)departments.get(i)).getID() == ID) {
				return (Department)departments.get(i);
			}
		}
		return null;
	}
	
	//returns item from department based on its ID
	public Item getItemWithID(int ID) {
		for(int i = 0; i < departments.size(); i++) {
			Department d = (Department)departments.get(i);
			Vector items = d.getItems();
			for(int j = 0; j < items.size(); j++) {
				Item e = (Item)items.get(j);
				if(e.getID() == ID) {
					return e;
				}
			}
		}
		return null;
	}
	
	//returns department based on item ID
	public Department getDepWithItemID(int ID) {
		for(int i = 0; i < departments.size(); i++) {
			Department d = (Department)departments.get(i);
			Vector items = d.getItems();
			for(int j = 0; j < items.size(); j++) {
				Item e = (Item)items.get(j);
				if(e.getID() == ID) {
					return d;
				}
			}
		}
		return null;
	}
	
	//returns department based on its name
	public Department getDepWithName(String name) {
		for(int i = 0; i < departments.size(); i++) {
			Department d = (Department)departments.get(i);
			if(d.getName().equalsIgnoreCase(name)) {
				return d;
			}
		}
		return null;
	}
}
