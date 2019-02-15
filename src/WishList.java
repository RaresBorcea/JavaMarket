import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.Vector;

public class WishList extends ItemList{
	private Strategy strategy;

	WishList(Comparator comp, Strategy strategy) {
		super(comp);
		this.strategy = strategy;
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
	
	//selects item to add to cart based on customer strategy
	public Item executeStrategy() {
		return strategy.execute(this);
	}
	
	public boolean add(Object e) {
		Node n = new Node((Item)e);
		double price = ((Item)e).getPrice();
		if(head == null) {
			head = n;
			return true;
		} else {
			Node current = head;
			while(current.next != null && comp.compare(n.data, current.data) > 0) {
				current = current.next;
			}
			if(comp.compare(n.data, current.data) == 0) {
				return false;
			} else {
				if(comp.compare(n.data, current.data) > 0) {
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
				return true;
			}
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
			return node.data;
		}
		return null;
	}
	
	//returns item from wishlist based in its ID
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
	
	//returns items from wishlist in alphabetical order
	public String toString() {
		Vector<Item> items = new Vector();
		String s = new String();
		s += "[";
		
		ListIterator it = listIterator();
	    while(it.hasNext()) {
	       Item e = (Item)it.next();
	       items.add(e);
	    }
	    
	    Collections.sort(items, new CompB());
	    for(int i = 0; i < items.size(); i++) {
	    	s += (Item)items.get(i);
		       if(i <= items.size() - 2) {
		    	   s += ", ";
		       }
	    }
	    
	    s += ']';
	    return s;
	}
}
