import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class Test {

    public static void main(String[] args) throws IOException {
        
        // reading store.txt
        RandomAccessFile file = new RandomAccessFile("store.txt","r");
        String line;
        String[] elem;
        line = file.readLine();
        Store store = Store.getInstance(line);
        while((line = file.readLine()) != null) {
            Department m;
            elem = line.split(";");
            switch(elem[0]) {
                    case "MusicDepartment":
                        m = (MusicDepartment) new MusicDepartment.MusicBuilder("MusicDepartment", Integer.parseInt(elem[1]))
                            .items()
                            .customers()
                            .build();
                        break;
                    
                    case "BookDepartment":
                        m = (BookDepartment) new BookDepartment.BookBuilder("BookDepartment", Integer.parseInt(elem[1]))
                            .items()
                            .customers()
                            .build();
                        break;
                    
                    case "VideoDepartment":
                        m = (VideoDepartment) new VideoDepartment.VideoBuilder("VideoDepartment", Integer.parseInt(elem[1]))
                            .items()
                            .customers()
                            .build();
                        break;
                    
                    default:
                        m = (SoftwareDepartment) new SoftwareDepartment.SoftwareBuilder("SoftwareDepartment", Integer.parseInt(elem[1]))
                            .items()
                            .customers()
                            .build();
            } 
            
            line = file.readLine();
            int noOfProducts = Integer.parseInt(line);
            for(int i = 0; i < noOfProducts; i++) {
                line = file.readLine();
                elem = line.split(";"); // using ; as element separator
                Item e = new Item(elem[0], Integer.parseInt(elem[1]), Double.parseDouble(elem[2]));
                m.addItem(e);
            }
            store.addDepartment(m);
        }
        file.close();
        
        // reading customers.txt
        file = new RandomAccessFile("customers.txt","r");
        line = file.readLine();
        int noOfCustomers = Integer.parseInt(line);
        for(int i = 0; i < noOfCustomers; i++) {
            line = file.readLine();
            elem = line.split(";");
            Strategy strategy;
            if(elem[2].equals("A")) {
                strategy = new StrategyA();
            } else if(elem[2].equals("B")) {
                strategy = new StrategyB();
            } else {
                strategy = new StrategyC();
            }
            Customer c = new Customer(elem[0], Double.parseDouble(elem[1]), strategy);
            store.enter(c);
        }
        file.close();
        
        // reading events.txt
        file = new RandomAccessFile("events.txt","r");
        line = file.readLine();
        PrintWriter out = new PrintWriter("output.txt"); //output file
        Department d;
        Item e;
        DateFormat dateFormat;
        Date date;
        String sDate;
        Notification n;
        Vector customers, items;
        Customer c;
        int noOfEvents = Integer.parseInt(line);
        for(int i = 0; i < noOfEvents; i++) {
            line = file.readLine();
            elem = line.split(";");
            switch(elem[0]) {
                case "addItem": // item added to customer list
                    for(int j = 0; j < store.getCustomers().size(); j++) {
                        if(((Customer)store.getCustomers().get(j)).getName().equals(elem[3])) {
                            e = store.getItemWithID(Integer.parseInt(elem[1]));
                            Item eNew = new Item(e.getName(), e.getID(), e.getPrice());
                            if(elem[2].equals("ShoppingCart")) { // to cart
                                ShoppingCart s = ((Customer)store.getCustomers().get(j)).getCart();
                                s.add(eNew);
                                d = store.getDepWithItemID(Integer.parseInt(elem[1]));
                                // adding customer as department customer
                                d.enter((Customer)store.getCustomers().get(j));
                            } else { // to wishlist
                                WishList w = ((Customer)store.getCustomers().get(j)).getWishes();
                                w.add(eNew);
                                d = store.getDepWithItemID(Integer.parseInt(elem[1]));
                                // adding customer as department observer
                                d.addObserver((Customer)store.getCustomers().get(j));
                            }
                        }
                    }
                    break;

                case "delItem": // item deleted from customer list
                    for(int j = 0; j < store.getCustomers().size(); j++) {
                        if(((Customer)store.getCustomers().get(j)).getName().equals(elem[3])) {
                            if(elem[2].equals("ShoppingCart")) {
                                ShoppingCart s = ((Customer)store.getCustomers().get(j)).getCart();
                                s.remove(s.getItemWithID(Integer.parseInt(elem[1])));
                            } else {
                                WishList w = ((Customer)store.getCustomers().get(j)).getWishes();
                                w.remove(w.getItemWithID(Integer.parseInt(elem[1])));
                                d = store.getDepWithItemID(Integer.parseInt(elem[1]));
                                items = d.getItems();
                                // checking whether customer remains observer
                                boolean stillObserver = false;
                                for(int k = 0; k < items.size(); k++) {
                                    Item item = (Item)items.get(k);
                                    e = w.getItemWithID(item.getID());
                                    if(e != null) {
                                        stillObserver = true;
                                    }
                                }
                                if(stillObserver == false) {
                                    d.removeObserver((Customer)store.getCustomers().get(j));
                                }
                            }
                        }
                    }
                    break;

                case "addProduct": // product added to store
                    d = store.getDepartment(Integer.parseInt(elem[1]));
                    e = new Item(elem[4], Integer.parseInt(elem[2]), Double.parseDouble(elem[3]));
                    d.addItem(e);
                    dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    date = new Date();
                    sDate = dateFormat.format(date);
                    // send notification to observers
                    n = new Notification(sDate, NotificationType.ADD, Integer.parseInt(elem[1]), Integer.parseInt(elem[2]));
                    d.notifyAllObservers(n);
                    break;

                case "modifyProduct": // product price modified
                    d = store.getDepartment(Integer.parseInt(elem[1]));
                    e = store.getItemWithID(Integer.parseInt(elem[2]));
                    e.setPrice(Double.parseDouble(elem[3]));
                    dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    date = new Date();
                    sDate = dateFormat.format(date);
                    n = new Notification(sDate, NotificationType.MODIFY, Integer.parseInt(elem[1]), Integer.parseInt(elem[2]));
                    d.notifyAllObservers(n);
                    // modify in carts
                    Vector custo = d.getCustomers();
                    for(int j = 0; j < custo.size(); j++) {
                        Customer cus = (Customer)custo.get(j);
                        ShoppingCart sCart = cus.getCart();
                        Item eToEdit = sCart.getItemWithID(e.getID());
                        if(eToEdit != null) {
                            eToEdit.setPrice(Double.parseDouble(elem[3]));
                        }
                    }
                    break;

                case "delProduct": // product deleted from store
                    e = store.getItemWithID(Integer.parseInt(elem[1]));
                    d = store.getDepWithItemID(Integer.parseInt(elem[1]));
                    d.removeItem(e);
                    dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    date = new Date();
                    sDate = dateFormat.format(date);
                    n = new Notification(sDate, NotificationType.REMOVE, d.getID(), Integer.parseInt(elem[1]));
                    d.notifyAllObservers(n);
                    // delete product from all carts
                    custo = d.getCustomers();
                    for(int j = 0; j < custo.size(); j++) {
                        Customer cus = (Customer)custo.get(j);
                        ShoppingCart sCart = cus.getCart();
                        Item eToRemove = sCart.getItemWithID(e.getID());
                        if(eToRemove != null) {
                            sCart.remove(eToRemove);
                        }
                    }
                    break;

                case "getItem": // get item based on strategy and add it to cart
                    customers = store.getCustomers();
                    for(int j = 0; j < customers.size(); j++) {
                        c = (Customer)customers.get(j);
                        if(c.getName().equals(elem[1])) {
                            if(((Item)c.getWishes().getItem(0)).getPrice() <= c.getCart().getBudget()) {
                                e = c.getWishes().executeStrategy();
                                out.println(e);
                                d = store.getDepWithItemID(e.getID());
                                c.getCart().add(e);
                                d.enter(c);
                                items = d.getItems();
                                boolean stillObserver = false;
                                for(int k = 0; k < items.size(); k++) {
                                    Item item = (Item)items.get(k);
                                    e = c.getWishes().getItemWithID(item.getID());
                                    if(e != null) {
                                        stillObserver = true;
                                    }
                                }
                                if(stillObserver == false) {
                                    d.removeObserver(c);
                                }
                            }
                        }
                    }
                    break;

                case "getItems": // print items from customer lists
                    customers = store.getCustomers();
                    for(int j = 0; j < customers.size(); j++) {
                        c = (Customer)customers.get(j);
                        if(c.getName().equals(elem[2])) {
                            if(elem[1].equals("ShoppingCart")) {
                                out.println(c.getCart().toString());
                            } else {
                                out.println(c.getWishes().toString());
                            }
                        }
                    }
                    break;

                case "getTotal": // get total price of items
                    customers = store.getCustomers();
                    for(int j = 0; j < customers.size(); j++) {
                        c = (Customer)customers.get(j);
                        if(c.getName().equals(elem[2])) {
                            if(elem[1].equals("ShoppingCart")) { //in cart
                                double price = c.getCart().getTotalPrice();
                                String pr = String.format(Locale.US, "%.2f", price);
                                out.println(pr);
                            } else { // in wishlist
                                double price = c.getWishes().getTotalPrice();
                                String pr = String.format(Locale.US, "%.2f", price);
                                out.println(pr);
                            }
                        }
                    }
                    break;

                case "accept": // apply accept benefits on customer cart
                    d = store.getDepartment(Integer.parseInt(elem[1]));
                    customers = store.getCustomers();
                    for(int j = 0; j < customers.size(); j++) {
                        c = (Customer)customers.get(j);
                        if(c.getName().equals(elem[2])) {
                            d.accept(c.getCart());
                        }
                    }
                    break;

                case "getObservers": // print department observers
                    d = store.getDepartment(Integer.parseInt(elem[1]));
                    out.println(d.getObservers());
                    break;

                case "getNotifications": // print customer notifications
                    customers = store.getCustomers();
                    for(int j = 0; j < customers.size(); j++) {
                        c = (Customer)customers.get(j);
                        if(c.getName().equals(elem[1])) {
                            out.println(c.notifToString());
                        }
                    }
                    break;

                default:
                    break;
                }
        }
        out.close();
    }
}
