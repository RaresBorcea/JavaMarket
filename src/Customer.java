import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class Customer implements Observer {
	private String name;
	private ShoppingCart cart;
	private WishList wishes;
	private Vector notifications;

	// builds customer fields using each one's strategy
	Customer(String name, double budget, Strategy strategy) {
		this.name = name;
		cart = new ShoppingCart(new CompCartAndA(), budget);
		// depending on strategies, we use different comparators
		if(strategy instanceof StrategyA) {
			wishes = new WishList(new CompCartAndA(), strategy);
		} else if(strategy instanceof StrategyB) {
			wishes = new WishList(new CompB(), strategy);
		} else {
			wishes = new WishList(new CompC(), strategy);
		}
		notifications = new Vector();
	}
	
	public String getName() {
		return name;
	}

	public ShoppingCart getCart() {
		return cart;
	}
	
	public void setCart(ShoppingCart cart) {
		this.cart = cart;
	}

	public WishList getWishes() {
		return wishes;
	}
	
	public Vector getNotifications() {
		return notifications;
	}

	public void update(Observable department, Object notification) {
		Notification n = (Notification) notification;
		NotificationType type = n.getType();
		notifications.add(n);
		double price = 0;
		int i;
		ListIterator it;
		Vector items;
		// modifies customer lists based on notification type
		switch(type) {
		case REMOVE:
			it = wishes.listIterator();
			i = -1;
			while(it.hasNext()) {
				Item item = (Item)it.next();
				i++;
				if(item.getID() == n.getItemID()) {
					wishes.remove(i);
				}
			}
			Department d = (Department)department;
			items = d.getItems();
			// after removing from wishlist, should check if customer is still an observer
			boolean stillObserver = false;
			for(int j = 0; j < items.size(); j++) {
				Item item = (Item)items.get(j);
				Item e = wishes.getItemWithID(item.getID());
				if(e != null) {
					stillObserver = true;
				}
			}
			if(stillObserver == false) {
				d.removeObserver(this);
			}
			it = cart.listIterator();
			i = -1;
			while(it.hasNext()) {
				Item item = (Item)it.next();
				i++;
				if(item.getID() == n.getItemID()) {
					cart.remove(i);
				}
			}
			break;
		case MODIFY:
			items = ((Department)department).getItems();
			for(i = 0; i < items.size(); i++) {
				if(((Item)items.get(i)).getID() == n.getItemID()) {
					price = ((Item)items.get(i)).getPrice();
				}
			}
			it = wishes.listIterator();
			while(it.hasNext()) {
				Item item = (Item)it.next();
				if(item.getID() == n.getItemID()) {
					item = (Item)wishes.remove(wishes.indexOf(item));
					item.setPrice(price);
					wishes.add(item);
				}
			}
			it = cart.listIterator();
			while(it.hasNext()) {
				Item item = (Item)it.next();
				if(item.getID() == n.getItemID()) {
					item = (Item)cart.remove(cart.indexOf(item));
					item.setPrice(price);
					cart.add(item);
				}
			}
			break;
		default:
			break;
		}
	}
	
	public String notifToString() {
		String s = new String();
		s += "[";
		for(int i = 0; i < notifications.size(); i++) {
			s += notifications.get(i);
			if(i <= notifications.size() - 2) {
				s += ", ";
			}
		}
	    s += ']';
	    return s;
	}
	
	public String toString() {
		return name;
	}
}
