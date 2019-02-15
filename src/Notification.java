
enum NotificationType {
	ADD, REMOVE, MODIFY
}

public class Notification {
	private String date;
	private NotificationType type;
	private int depID;
	private int itemID;
	
	Notification(String date, NotificationType type, int depID, int itemID) {
		this.date = date;
		this.type = type;
		this.depID = depID;
		this.itemID = itemID;
	}
	
	public String getDate() {
		return date;
	}

	public NotificationType getType() {
		return type;
	}

	public int getDepID() {
		return depID;
	}

	public int getItemID() {
		return itemID;
	}
	
	public String toString() {
		String s = new String();
		if(type.equals(NotificationType.ADD)) {
			s += "ADD;";
		} else if(type.equals(NotificationType.MODIFY)) {
			s += "MODIFY;";
		} else {
			s += "REMOVE;";
		}
		s += itemID + ";" + depID;
		return s;
	}
	
	//for the interface, also display the Date the notification was sent
	public String toStringForInterface() {
		String s = new String();
		s += date + ": Type - ";
		if(type.equals(NotificationType.ADD)) {
			s += "ADD";
		} else if(type.equals(NotificationType.MODIFY)) {
			s += "MODIFY";
		} else {
			s += "REMOVE";
		}
		s += ", for item " + itemID + ", from dep. " + depID;
		return s;
	}
}
