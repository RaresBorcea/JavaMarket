import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

class CompCartAndA implements Comparator<Item> {
	// comparator for cart and StrategyA
	public int compare(Item o1, Item o2) {
		double p1 = o1.getPrice();
		double p2 = o2.getPrice();
		if(p1 < p2) {
			return -1;
		} else if(p1 > p2) {
			return 1;
		} else if(o1.getName().compareTo(o2.getName()) < 0) {
			return -1;
		} else if(o1.getName().compareTo(o2.getName()) > 0) {
			return 1;
		} else if(o1.getID() - o2.getID() < 0) {
			return -1;
		} else if(o1.getID() - o2.getID() > 0) {
			return 1;
		} else return 0;
	}
}

class CompB implements Comparator<Item> {
	// comparator for StrategyB
	public int compare(Item o1, Item o2) {
		if(o1.getName().compareTo(o2.getName()) < 0) {
			return -1;
		} else if(o1.getName().compareTo(o2.getName()) > 0) {
			return 1;
		} else if(o1.getID() - o2.getID() < 0) {
			return -1;
		} else if(o1.getID() - o2.getID() > 0) {
			return 1;
		} else return 0;
	}
}

class CompC implements Comparator<Item> {
	// comparator for StrategyC
	public int compare(Item o1, Item o2) {
		return -1;
	}
}

class CompDesc implements Comparator<Item> {
	// comparator for descending prices
	public int compare(Item o2, Item o1) {
		double p1 = o1.getPrice();
		double p2 = o2.getPrice();
		if(p1 < p2) {
			return -1;
		} else if(p1 > p2) {
			return 1;
		} else if(o1.getName().compareTo(o2.getName()) < 0) {
			return -1;
		} else if(o1.getName().compareTo(o2.getName()) > 0) {
			return 1;
		} else if(o1.getID() - o2.getID() < 0) {
			return -1;
		} else if(o1.getID() - o2.getID() > 0) {
			return 1;
		} else return 0;
	}
}

public abstract class ItemList<Item> {
	protected Node<Item> head;
	protected Comparator comp;
	
	ItemList(Comparator comp) {
		this.comp = comp;
		head = null;
	}
	
	static class Node<Item> {
		protected Node<Item> next;
		protected Node<Item> previous;
		protected Item data;
		
		public Node(Item data) {
			this.next = null;
			this.previous = null;
			this.data = data;
		}
	}
	
	class ItemIterator<Item> implements ListIterator<Item> {
		private Node current = null;
		private int index;
		
		ItemIterator(int index) {
			this.index = index;
			this.current = head;
			int i = 0;
			while(i < index) {
				if(current != null) {
					current = current.next;
					i++;
				}
			}
		}
		
		ItemIterator() {
			this.index = 0;
			this.current = head;
		}
		
		public boolean hasNext() {
			return current != null;
		}

		public boolean hasPrevious() {
			return current != null;
		}
		
		public Item next() {
			if(hasNext()) { 
				Item e = (Item)current.data;
				current = current.next;
				index++;
				return e;
			}
			return null;
		}

		public int nextIndex() {
			return (index + 1);
		}

		public Item previous() {
			if(hasPrevious()) { 
				Item e = (Item)current.data;
				current = current.previous;
				index--;
				return e;
			}
			return null;
		}

		public int previousIndex() {
			return (index - 1);
		}

		public void add(Item e) throws UnsupportedOperationException {}

		public void remove() throws UnsupportedOperationException {}

		public void set(Item e) throws UnsupportedOperationException {}
	}
	
	public abstract boolean add(Item e);
	
	public boolean addAll(Collection<? extends Item> c) {
		boolean result = false;
		Iterator it = c.iterator();
	    while(it.hasNext()) {
	       Item e = (Item)it.next();
	       boolean rez = add(e);
	       if(rez) {
	    	   result = true;
	       }
	    }
	    return result;
	}
	
	public Item getItem(int index) {
		ListIterator it = listIterator(index);
		return (Item)it.next();
	}
	
	public Node<Item> getNode(int index) {
		int ind = 0;
		Node current = head;
		while(current != null && ind < index) {
			current = current.next;
			ind++;
		}
		if(ind == index) {
			return current;
		} else {
			return null;
		}
	}
	
	public int indexOf(Item e) {
		ListIterator it = listIterator();
		int i = 0;
		while(it.hasNext()) {
			Item item = (Item)it.next();
			if(item.equals(e)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public int indexOf(Node<Item> node) {
		ListIterator it = listIterator();
		int i = 0;
		while(it.hasNext()) {
			Item item = (Item)it.next();
			if(item.equals(node.data)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public boolean contains(Node<Item> node) {
		ListIterator it = listIterator();
		int i = 0;
		while(it.hasNext()) {
			Item item = (Item)it.next();
			if(item.equals(node.data)) {
				return true;
			}
			i++;
		}
		return false;
	}
	
	public boolean contains(Item e) {
		ListIterator it = listIterator();
		int i = 0;
		while(it.hasNext()) {
			Item item = (Item)it.next();
			if(item.equals(e)) {
				return true;
			}
			i++;
		}
		return false;
	}
	
	public abstract Item remove(int index);
	
	public boolean remove(Item e) {
		int i = indexOf(e);
		if(i != -1) {
			remove(i);
			return true;
		}
		return false;
	}
	public boolean removeAll(Collection<? extends Item> c) {
		boolean result = false;
		Iterator it = c.iterator();
	    while(it.hasNext()) {
	       Item e = (Item)it.next();
	       boolean rez = remove(e);
	       if(rez) {
	    	   result = true;
	       }
	    }
	    return result;
	}
	
	public boolean isEmpty() {
		if(head == null) {
			return true;
		} else {
			return false;
		}
	}
	
	public ListIterator<Item> listIterator(int index) {
		return new ItemIterator(index);
	}
	
	public ListIterator<Item> listIterator() {
		return new ItemIterator();
	}
	
	public String toString() {
		String s = new String();
		s += "[";
		ListIterator it = listIterator();
	    while(it.hasNext()) {
	       Item e = (Item)it.next();
	       s += e;
	       if(it.hasNext()) {
	    	   s += ", ";
	       }
	    }
	    s += ']';
	    return s;
	}
	
	public abstract Double getTotalPrice();
}
