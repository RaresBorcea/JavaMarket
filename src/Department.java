import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public abstract class Department extends Observable implements Subject{
	private String name;
	private Vector items;
	private Vector customers;
	private int ID;
	
	//using the builder pattern to create store departments
	protected Department(DepartmentBuilder builder) {
		this.name = builder.name;
		this.ID = builder.ID;
		this.items = builder.items;
		this.customers = builder.customers;
	}
	
	public void enter(Customer c) {
		if(!customers.contains(c)) {
			customers.add(c);
		}
	}
	
	public void exit(Customer c) {
		customers.remove(c);
	}
	
	public String getName() {
		return name;
	}
	
	public Vector getCustomers() {
		return customers;
	}
	
	public int getID() {
		return ID;
	}
	
	public void addItem(Item e) {
		if(!items.contains(e)) {
			items.add(e);
		}
	}
	
	public void removeItem(Item e) {
		items.remove(e);
	}
	
	public Vector getItems() {
		return items;
	}
	
	public void addObserver(Customer c) {
		super.addObserver(c);
	}
	
	public void removeObserver(Customer c) {
		deleteObserver(c);
	}
	
	public void notifyAllObservers(Notification n) {
		setChanged();
		notifyObservers(n);
	}
	
	//returns observers as a String
	public String getObservers() {
		String s = new String();
		s += "[";
		//using reflections to get observers from Observable superclass
		Field f;
		try {
			f = Department.class.getSuperclass().getDeclaredField("obs");
		} catch (NoSuchFieldException e) {
			s += "]";
			return s;
		}
		//this way, we made the field accessible
		f.setAccessible(true);
		Vector set; 
		try {
			set = (Vector) f.get(this);
		} catch (IllegalAccessException e) {
			s += "]";
			return s;
		}
		Iterator it = set.iterator();
        while(it.hasNext()){
        	Customer c = (Customer)it.next();
 	        s += c.getName();
 	        if(it.hasNext()) {
 	     	   s += ", ";
 	        }
        }
        s += ']';
	    return s;
	}
	
	//returns observers as a Vector of customers
	public Vector<Customer> getVectorObservers() {
		Vector<Customer> s = new Vector();
		Field f;
		try {
			f = Department.class.getSuperclass().getDeclaredField("obs");
		} catch (NoSuchFieldException e) {
			return null;
		}
		f.setAccessible(true);
		Vector set; 
		try {
			set = (Vector) f.get(this);
		} catch (IllegalAccessException e) {
			return null;
		}
		Iterator it = set.iterator();
        while(it.hasNext()){
        	Customer c = (Customer)it.next();
 	        s.add(c);
        }
	    return s;
	}
	
	public abstract void accept(ShoppingCart s);
	
	public static abstract class DepartmentBuilder {
		private String name;
		private Vector items;
		private Vector customers;
		private int ID;
		
		public DepartmentBuilder(String name, int ID) {
			this.name = name;
			this.ID = ID;
		}
		
		public DepartmentBuilder items() {
			this.items = new Vector();
			return this;
		}
		
		public DepartmentBuilder customers() {
			this.customers = new Vector();
			return this;
		}
		
		public abstract Department build();
	}
	
	//checks whether a department contains an element by its ID
	public boolean containsID(int ID) {
		for(int i = 0; i < items.size(); i++) {
			if(((Item)items.get(i)).getID() == ID) {
				return true;
			}
		}
		return false;
	}
	
	//returns most bought item from this department
	public Item mostBought() {
		int max = 0;
		Item most = null;
		for(int i = 0; i < items.size(); i++) {
			Item e = (Item)items.get(i);
			int rep = 0;
			for(int j = 0; j < customers.size(); j++) {
				Customer c = (Customer)customers.get(j);
				ShoppingCart s = c.getCart();
				ListIterator it = s.listIterator();
				while(it.hasNext()) {
					if(((Item)it.next()).getID() == e.getID()) {
						rep++;
					}
				}
			}
			if(rep > max) {
				max = rep;
				most = e;
			}
		}
		return most;
	}
	
	//returns most wanted item from this department
	public Item mostWanted() {
		int max = 0;
		Item most = null;
		for(int i = 0; i < items.size(); i++) {
			Item e = (Item)items.get(i);
			int rep = 0;
			Vector<Customer> obs = getVectorObservers();
			for(int j = 0; j < obs.size(); j++) {
				Customer c = obs.get(j);
				WishList w = c.getWishes();
				ListIterator it = w.listIterator();
				while(it.hasNext()) {
					if(((Item)it.next()).getID() == e.getID()) {
						rep++;
					}
				}
			}
			if(rep > max) {
				max = rep;
				most = e;
			}
		}
		return most;
	}
	
	//returns most expensive item from this department
	public Item mostExpensive() {
		Item most = null;
		double max = 0;
		for(int i = 0; i < items.size(); i++) {
			Item e = (Item)items.get(i);
			if(e.getPrice() > max) {
				most = e;
				max = e.getPrice();
			}
		}
		return most;
	}
}
