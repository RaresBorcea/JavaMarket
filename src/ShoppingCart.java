import java.util.Comparator;
import java.util.ListIterator;
import java.util.Vector;

public class ShoppingCart extends ItemList implements Visitor {
	private double budget;

	ShoppingCart(Comparator comp, double budget) {
		super(comp);
		this.budget = budget;
	}

	public double getBudget() {
		return budget;
	}
	
	public void addToBudget(double amount) {
		budget += amount;
	}
	
	public void subFromBudget(double amount) {
		budget -= amount;
	}

	// called from the accept methods; visits and applies discounts
	public void visit(BookDepartment bookDepartment) {
		ListIterator it = listIterator();
		double price;
		while(it.hasNext()) {
			Item item = (Item)it.next();
			if(bookDepartment.containsID(item.getID())) {
				price = item.getPrice();
				item = (Item)remove(indexOf(item));
				item.setPrice((double)0.9 * price);
				add(item);
			}
		}
	}

	public void visit(MusicDepartment musicDepartment) {
		ListIterator it = listIterator();
		double price = 0;
		while(it.hasNext()) {
			Item item = (Item)it.next();
			if(musicDepartment.containsID(item.getID())) {
				price += item.getPrice();
			}
		}
		addToBudget((double)0.1 * price);
	}

	public void visit(SoftwareDepartment softwareDepartment) {
		double min = 1000000;
		Vector<Item> items = softwareDepartment.getItems();
		for(Item e : items) {
			if(e.getPrice() < min) {
				min = e.getPrice();
			}
		}
		if(budget < min) {
			ListIterator it = listIterator();
			double price;
			while(it.hasNext()) {
				Item item = (Item)it.next();
				if(softwareDepartment.containsID(item.getID())) {
					price = item.getPrice();
					item = (Item)remove(indexOf(item));
					item.setPrice((double)0.8 * price);
					add(item);
				}
			}
		}
	}

	public void visit(VideoDepartment videoDepartment) {
		Vector<Item> items = videoDepartment.getItems();
		ListIterator it = listIterator();
		double price = 0;
		while(it.hasNext()) {
			Item item = (Item)it.next();
			if(videoDepartment.containsID(item.getID())) {
				price += item.getPrice();
			}
		}
		double max = 0;
		for(Item e : items) {
			if(e.getPrice() > max) {
				max = e.getPrice();
			}
		}
		addToBudget((double)0.05 * price);
		if(price > max) {
			it = listIterator();
			while(it.hasNext()) {
				Item item = (Item)it.next();
				if(videoDepartment.containsID(item.getID())) {
					price = item.getPrice();
					item = (Item)remove(indexOf(item));
					item.setPrice((double)0.85 * price);
					add(item);
				}
			}
		}
	}

	public Double getTotalPrice() {
		ListIterator it = listIterator();
		double s = 0;
		while(it.hasNext()) {
			Item item = (Item)it.next();
			s += item.getPrice();
		}
		return (Double)s;
	}

	public boolean add(Object e) {
		Node n = new Node((Item)e);
		double price = ((Item)e).getPrice();
		if(price > budget) {
			return false;
		}
		if(head == null) {
			head = n;
			subFromBudget(price);
			return true;
		} else {
			Node current = head;
			while(current.next != null && comp.compare(n.data, current.data) > 0) {
				current = current.next;
			}
			if(comp.compare(n.data, current.data) > 0 || comp.compare(n.data, current.data) == 0) {
				n.next = current.next;
				if(current.next != null) {
					current.next.previous = n;
				}
				n.previous = current;
				current.next = n;
			} else if(current != head) {
				n.next = current;
				n.previous = current.previous;
				current.previous.next = n;
				current.previous = n;
			} else {
				head.previous = n;
				n.next = head;
				head = n;
			}
			subFromBudget(price);
			return true;
		}
	}
	

	public Object remove(int index) {
		Node<Item> node = getNode(index);
		if(node != null) {
			if(node.previous != null) {
				node.previous.next = node.next;
			}
			if(node.next != null) {
				node.next.previous = node.previous;
			}
			if(node == head) {
				head = node.next;
			}
			double price = ((Item)node.data).getPrice();
			addToBudget(price);
			return node.data;
		}
		return null;
	}
	
	//returns item from cart based on its ID
	public Item getItemWithID(int ID) {
		ListIterator it = listIterator();
		while(it.hasNext()) {
			Item item = (Item)it.next();
			if(item.getID() == ID) {
				return item;
			}
		}
		return null;
	}
}
