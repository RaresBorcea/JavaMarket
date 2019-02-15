import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

public class TestInterface extends JFrame {
	private Store store = null;
	JPanel panel, main;
	Item itemToEdit = null;
	Customer customerToEdit = null;
	JPanel cartPanel, addToCart, wishPanel, addToWish, notifPanel, depPanel;
	DefaultListModel<Item> items;
	JLabel budget, total;
	JList customerList, itemList;
	Department depToShow = null;
	
	public TestInterface(){
        super("Store Manager");
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(300, 200));
        getContentPane().setBackground(Color.white);
        setLayout(new FlowLayout());
        panel = new MainPanel();
        add(panel);
        
        show();
        pack();
    }
	
	public class MainPanel extends JPanel {
		JFileChooser fc = new JFileChooser();
		
		MainPanel(){
			JPanel welcome = new JPanel();
			welcome.setBorder(new EmptyBorder(10, 10, 10, 10));
            welcome.setLayout(new GridBagLayout());
            //using constraints for a fluent welcome panel
            GridBagConstraints c = new GridBagConstraints();
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.anchor = GridBagConstraints.NORTH;

            //using HTML for font settings
            JLabel title = new JLabel("<html><h1><i><strong>Welcome to Store Manager!</strong></i></h1></html>");
            JLabel create = new JLabel("<html><h3><strong><center><p>Let's first create your store.</p>"
            			+ "<p>Please upload the files coresponding to:</p></center></strong></h3><hr><br></html>");
            welcome.add(title, c);
            welcome.add(create, c);
            JLabel error = new JLabel("");
            error.setVisible(false);
            
            JButton store = new JButton("Store details");
            JButton customer = new JButton("Customer details");
            store.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt");
                	fc.setFileFilter(filter);
                    int returnVal = fc.showOpenDialog(null);
                    if(returnVal == JFileChooser.APPROVE_OPTION) {
                    	File file = fc.getSelectedFile();
                    	try {
							readStore(file.getPath());
							error.setVisible(false);
							customer.setEnabled(true);
						} catch (IOException e1) { //exception for file not found
							error.setText("File not found");
							error.setVisible(true); //display error message
							customer.setEnabled(false);
						} catch (badFile e1) { //display error if wrong file selected
							error.setText("Invalid store file. It should be store.txt!");
							error.setVisible(true);
							customer.setEnabled(false);
						}
                    }
                }
            });
            
            customer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt");
                	fc.setFileFilter(filter);
                    int returnVal = fc.showOpenDialog(null);
                    if(returnVal == JFileChooser.APPROVE_OPTION) {
                    	File file = fc.getSelectedFile();
                    	try {
							readCustomers(file.getPath());
							welcome.setVisible(false);
							//after both files are selected, display main store page
							mainPage manage = new mainPage();
							add(manage);
							show();
							pack();
							
						} catch (IOException e1) {
							error.setText("File not found");
							error.setVisible(true);
						} catch (badFile e1) {
							error.setText("Invalid customers file. It should be customers.txt!");
							error.setVisible(true);
						}
                    }
                }
            });
            customer.setEnabled(false);

            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.HORIZONTAL;

            JPanel buttons = new JPanel(new GridBagLayout());
            buttons.add(store, c);
            buttons.add(customer, c);

            c.weighty = 1;
            welcome.add(buttons, c);
            welcome.add(error, c);
            add(welcome);
		}
		
		public class badFile extends Exception {
			  public badFile() { //exception for wrongly selected file
				  super(); 
			  }
			}
		
		//reads store.txt
		public void readStore(String filePath) throws IOException, badFile {
			File f = new File(filePath);
			String fileName = f.getName();
			if(!fileName.equals("store.txt")) {
				throw new badFile();
			}
			
			RandomAccessFile file = new RandomAccessFile(filePath,"r");
	    	String line;
	    	String[] elem;
	    	line = file.readLine();
	    	store = Store.getInstance(line);
	    	
			while((line = file.readLine()) != null) {
				Department m;
				elem = line.split(";");
				if(elem[0].equals("MusicDepartment")) {
					m = (MusicDepartment) new MusicDepartment.MusicBuilder("MusicDepartment", Integer.parseInt(elem[1]))
							.items()
							.customers()
							.build();
				} else if(elem[0].equals("BookDepartment")) {
					m = (BookDepartment) new BookDepartment.BookBuilder("BookDepartment", Integer.parseInt(elem[1]))
							.items()
							.customers()
							.build();
				} else if(elem[0].equals("VideoDepartment")) {
					m = (VideoDepartment) new VideoDepartment.VideoBuilder("VideoDepartment", Integer.parseInt(elem[1]))
							.items()
							.customers()
							.build();
				} else {
					m = (SoftwareDepartment) new SoftwareDepartment.SoftwareBuilder("SoftwareDepartment", Integer.parseInt(elem[1]))
							.items()
							.customers()
							.build();
				}
				line = file.readLine();
				int noOfProducts = Integer.parseInt(line);
				for(int i = 0; i < noOfProducts; i++) {
					line = file.readLine();
					elem = line.split(";");
					Item e = new Item(elem[0], Integer.parseInt(elem[1]), Double.parseDouble(elem[2]));
					m.addItem(e);
				}
				store.addDepartment(m);
			}
			file.close();
		}
		
		//reads customers.txt
		public void readCustomers(String filePath) throws IOException, badFile {
			File f = new File(filePath);
			String fileName = f.getName();
			if(!fileName.equals("customers.txt")) {
				throw new badFile();
			}
			
			RandomAccessFile file = new RandomAccessFile(filePath,"r");
			String line;
	    	String[] elem;
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
		}
	}
	
	//main display function
	public class mainPage extends JPanel {
		mainPage() {
			setBorder(new EmptyBorder(10, 10, 10, 10));
			JPanel container = new JPanel(); //main container
			container.setLayout(new FlowLayout());
			main = new JPanel();
			JPanel itemAndClients = new JPanel();
			main.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.anchor = GridBagConstraints.NORTH;
            c.gridx = 0;
			
			JLabel title = new JLabel("<html><h3><strong><center>This is your store:</center></strong></h3><hr><br></html>");
			main.add(title, c);
			itemAndClients.setLayout(new GridBagLayout());
			
			JPanel itemPanel = new JPanel();
			itemPanel.setLayout(new FlowLayout());
	        JPanel customerPanel = new JPanel();
	        customerPanel.setLayout(new FlowLayout());
	        
	        //list for store items
	        Vector<Item> itemVector = new Vector();
	        for(int i = 0; i < store.getDepartments().size(); i++) {
	        	for(int j = 0; j < ((Department)store.getDepartments().get(i)).getItems().size(); j++) {
	        		itemVector.add((Item)((Department)store.getDepartments().get(i)).getItems().get(j));
	        	}
	        }
	        
	        items = new DefaultListModel();
	        for(int i = 0; i < itemVector.size(); i++) {
	        	items.addElement(itemVector.get(i));
	        }
	        itemList = new JList(items);
	        itemList.setCellRenderer(new ItemRenderer());
	        itemPanel.add(new JScrollPane(itemList));
	        
	        //list for store customers
	        customerList = new JList(store.getCustomers());
	        customerPanel.add(new JScrollPane(customerList));
	        
	        JLabel itemTitle = new JLabel("Items:");
	        JLabel customerTitle = new JLabel("Customers:");
	        
	        c.fill = GridBagConstraints.HORIZONTAL;
	        c.weightx = 0.5;
	        c.gridx = 0;
	        c.gridy = 1;
	        itemAndClients.add(itemTitle, c);
	        c.gridx = 1;
	        c.fill = GridBagConstraints.REMAINDER;
	        itemAndClients.add(customerTitle, c);
	        
            c.fill = GridBagConstraints.HORIZONTAL;
	        c.anchor = GridBagConstraints.WEST;
	        c.gridx = 0;
	        c.gridy = 2;
            
            itemAndClients.add(itemPanel);
            c.anchor = GridBagConstraints.EAST;
	        itemAndClients.add(customerPanel);
	        main.add(itemAndClients, c);
	        
	        c.gridwidth = 1;
	        c.gridx = 0;
	        c.gridy = 3;
	        //sort items alphabetically
	        JButton alphabetically = new JButton("ABC order");
	        alphabetically.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	Collections.sort(itemVector, new CompB());
                	items.clear();
                	for(int i = 0; i < itemVector.size(); i++) {
        	        	items.addElement(itemVector.get(i));
        	        }
                	invalidate();
                	validate();
                	repaint();
                }
            });
	        main.add(alphabetically, c);
	        
	        c.gridx = 1;
	        //sort items by price - ascending
	        JButton priceAsc = new JButton("Price ↑");
	        priceAsc.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	Collections.sort(itemVector, new CompCartAndA());
                	items.clear();
                	for(int i = 0; i < itemVector.size(); i++) {
        	        	items.addElement(itemVector.get(i));
        	        }
                	invalidate();
                	validate();
                	repaint();
                }
            });
	        main.add(priceAsc, c);
	        
	        c.gridx = 2;
	        //sort items by price - descending
	        JButton priceDes = new JButton("Price ↓");
	        priceDes.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	Collections.sort(itemVector, new CompDesc());
                	items.clear();
                	for(int i = 0; i < itemVector.size(); i++) {
        	        	items.addElement(itemVector.get(i));
        	        }
                	invalidate();
                	validate();
                	repaint();
                }
            });
	        main.add(priceDes, c);
	        
	        c.gridx = 3;
	        //blank space
	        JLabel blank = new JLabel("    ");
	        main.add(blank, c);
	        
	        c.gridx = 4;
	        JButton cart = new JButton("Cart");
	        cartPanel = new JPanel();
	        cartPanel.setVisible(false);
	        add(cartPanel);
	        addToCart = new JPanel();
	        addToCart.setVisible(false);
	        add(addToCart);
	        //display cart after customer selection
	        cart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					customerToEdit = (Customer)customerList.getSelectedValue();
	        		if(customerToEdit == null) {
	        			return;
	        		}
	        		main.setVisible(false);
					cartPanel.setVisible(true);
					renderCart();
					show();
			        pack();
				}
	        });
	        
	        main.add(cart, c);
	        c.gridx = 5;
	        JButton wish = new JButton("WishList");
	        wishPanel = new JPanel();
	        wishPanel.setVisible(false);
	        add(wishPanel);
	        addToWish = new JPanel();
	        addToWish.setVisible(false);
	        add(addToWish);
	        //display wishlist after customer selection
	        wish.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					customerToEdit = (Customer)customerList.getSelectedValue();
	        		if(customerToEdit == null) {
	        			return;
	        		}
	        		main.setVisible(false);
	        		wishPanel.setVisible(true);
					renderWish();
					show();
			        pack();
				}
	        });
	        main.add(wish, c);
	        
	        c.gridx = 6;
	        JButton notif = new JButton("Notifications");
	        notifPanel = new JPanel();
	        notifPanel.setVisible(false);
	        add(notifPanel);
	        //display notifications after customer selection
	        notif.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					customerToEdit = (Customer)customerList.getSelectedValue();
	        		if(customerToEdit == null) {
	        			return;
	        		}
	        		main.setVisible(false);
	        		notifPanel.setVisible(true);
					renderNotif();
					show();
			        pack();
				}
	        });
	        main.add(notif, c);
	        
	        //add a product panel
	        JPanel addProd = new JPanel();
	        addProd.setLayout(new GridLayout(5,1));
	        JLabel titleAdd = new JLabel("<html><h3><strong>Add a product:</h3></strong><hr></html>", SwingConstants.CENTER);
	        addProd.add(titleAdd);
	        JPanel prodDetails = new JPanel();
	        prodDetails.setLayout(new GridLayout(4,2));
	        JLabel pName = new JLabel("Name: ");
	        prodDetails.add(pName);
	        JTextField prodName = new JTextField(15);
	        prodDetails.add(prodName);
	        JLabel pID = new JLabel("ID: ");
	        prodDetails.add(pID);
	        JTextField prodID = new JTextField(15);
	        prodDetails.add(prodID);
	        JLabel pPrice = new JLabel("Price: ");
	        prodDetails.add(pPrice);
	        JTextField prodPrice = new JTextField(15);
	        prodDetails.add(prodPrice);
	        JLabel pDep = new JLabel("Department: ");
	        prodDetails.add(pDep);
	        JTextField prodDep = new JTextField(15);
	        prodDetails.add(prodDep);
	        JLabel warning = new JLabel("");
	        warning.setVisible(false);
	        JPanel buttonsProd = new JPanel();
	        buttonsProd.setLayout(new GridLayout(1,2));
	        JButton addProdBtn = new JButton("Add");
	        addProdBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	String s, nam, dpName;
                	Double pric;
                	int pdID;
                	s = prodName.getText();
                	if(s.equals("")) { //check each field is filled
                		warning.setText("Product name required!");
                		warning.setVisible(true);
                		return;
                	}
                	nam = new String(s);
                	s = prodID.getText();
                	if(s.equals("")) {
                		warning.setText("Product ID required!");
                		warning.setVisible(true);
                		return;
                	}
                	pdID = Integer.parseInt(s);
                	s = prodPrice.getText();
                	if(s.equals("")) {
                		warning.setText("Product price required!");
                		warning.setVisible(true);
                		return;
                	}
                	pric = Double.parseDouble(s);
                	s = prodDep.getText();
                	if(s.equals("")) {
                		warning.setText("Product department required!");
                		warning.setVisible(true);
                		return;
                	}
                	dpName = new String(s);
                	if(store.getItemWithID(pdID) != null) {
                		warning.setText("Product already added!");
                		warning.setVisible(true);
                		return;
                	}
                	Department d = store.getDepWithName(dpName);
        			Item eNew = new Item(nam, pdID, pric);
        			d.addItem(eNew);
        			items.addElement(eNew);
        			itemVector.add(eNew);
        			//send notification to observers
        			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        			Date date = new Date();
        			String sDate = dateFormat.format(date);
        			Notification n = new Notification(sDate, NotificationType.ADD, d.getID(), pdID);
        			d.notifyAllObservers(n);
        			main.setVisible(true);
        			//reset fields and hide warnings
        			addProd.setVisible(false);
        			prodName.setText("");
        			prodID.setText("");
        			prodPrice.setText("");
        			prodDep.setText("");
        			warning.setVisible(false);
        			show();
			        pack();
                }
            });
	        
	        JButton cancelProdBtn = new JButton("Cancel");
	        //cancel adding a product
	        cancelProdBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	main.setVisible(true);
        			addProd.setVisible(false);
        			prodName.setText("");
        			prodID.setText("");
        			prodPrice.setText("");
        			prodDep.setText("");
        			warning.setVisible(false);
        			show();
			        pack();
                }
            });
	        buttonsProd.add(addProdBtn);
	        buttonsProd.add(cancelProdBtn);
	        addProd.add(prodDetails);
	        JLabel line = new JLabel("<html><hr></html>");
	        addProd.add(line);
	        addProd.add(buttonsProd);
	        addProd.add(warning);
	        
	        c.gridy = 4;
	        c.gridx = 0;
	        JButton add = new JButton("Add");
	        //finish adding product and return to store
	        add.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					main.setVisible(false);
					addProd.setVisible(true);
					show();
			        pack();
				}
	        });
	        main.add(add, c);
	        addProd.setVisible(false);
	        container.add(addProd);
	        
	        c.gridx = 1;
	        JButton remove = new JButton("Remove");
	        //remove product from store
	        remove.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	                ListSelectionModel selItem = itemList.getSelectionModel();
	                Item item = (Item)itemList.getSelectedValue();
	                int i = selItem.getMinSelectionIndex();
	                if (i >= 0) {
	                	items.remove(i);
		                itemVector.remove(item);
		                int id = item.getID();
		                Department d = (Department)store.getDepWithItemID(id);
		                d.removeItem(item);
		                //notify observers
		                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		                Date date = new Date();
		    			String sDate = dateFormat.format(date);
		    			Notification n = new Notification(sDate, NotificationType.REMOVE, d.getID(), id);
		    			d.notifyAllObservers(n);
		    			//delete product from carts
		    			Vector custo = d.getCustomers();
		    			for(int j = 0; j < custo.size(); j++) {
		    				Customer cus = (Customer)custo.get(j);
		    				ShoppingCart sCart = cus.getCart();
		    				Item eToRemove = sCart.getItemWithID(item.getID());
		    				if(eToRemove != null) {
		    					sCart.remove(eToRemove);
		    				}
		    			}
	                }
	            }
	        });
	        main.add(remove, c);
	    
	        c.gridx = 2;
	        //edit a product button
	        JPanel editProd = new JPanel();
	        editProd.setLayout(new GridLayout(3,1));
	        JButton modify = new JButton("Edit");
	        modify.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		itemToEdit = (Item)itemList.getSelectedValue();
	        		if(itemToEdit == null) {
	        			return;
	        		}
	                main.setVisible(false);
	                editProd.setVisible(true);
	                show();
			        pack();
	            }
	        });
	        main.add(modify, c);
	        
	        c.gridy = 5;
	        c.gridx = 0;
	        JLabel blank2 = new JLabel("    ");
	        main.add(blank2, c);
	        
	        c.gridy = 6;
	        c.gridx = 0;
	        //panel for department display
	        depPanel = new JPanel();
	        depPanel.setVisible(false);
	        add(depPanel);
	        
	        //buttons for each department display
	        JButton bookDepartment = new JButton("BookDepartment");
	        bookDepartment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					depToShow = store.getDepWithName("BookDepartment");
	        		if(depToShow == null) {
	        			return;
	        		}
					renderDep();
					main.setVisible(false);
	        		depPanel.setVisible(true);
					show();
			        pack();
				}
	        });
	        main.add(bookDepartment, c);
	        
	        c.gridx = 1;
	        JButton musicDepartment = new JButton("MusicDepartment");
	        musicDepartment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					depToShow = store.getDepWithName("MusicDepartment");
	        		if(depToShow == null) {
	        			return;
	        		}
					renderDep();
					main.setVisible(false);
	        		depPanel.setVisible(true);
					show();
			        pack();
				}
	        });
	        main.add(musicDepartment, c);
	        
	        c.gridy = 7;
	        c.gridx = 0;
	        JButton softwareDepartment = new JButton("SoftwareDepartment");
	        softwareDepartment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					depToShow = store.getDepWithName("SoftwareDepartment");
	        		if(depToShow == null) {
	        			return;
	        		}
					renderDep();
					main.setVisible(false);
	        		depPanel.setVisible(true);
					show();
			        pack();
				}
	        });
	        main.add(softwareDepartment, c);
	        c.gridx = 1;
	        JButton videoDepartment = new JButton("VideoDepartment");
	        videoDepartment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					depToShow = store.getDepWithName("VideoDepartment");
	        		if(depToShow == null) {
	        			return;
	        		}
					renderDep();
					main.setVisible(false);
	        		depPanel.setVisible(true);
					show();
			        pack();
				}
	        });
	        main.add(videoDepartment, c);
	        
	        //edit a product panel
	        JLabel titleEdit = new JLabel("<html><h3><strong>Edit product price:</h3></strong><hr></html>", SwingConstants.CENTER);
	        editProd.add(titleEdit);
	        JPanel prodPriceEdit = new JPanel();
	        prodPriceEdit.setLayout(new GridLayout(2,2));
	        JLabel pEditPrice = new JLabel("New Price: ");
	        prodPriceEdit.add(pEditPrice);
	        JTextField pNewPrice = new JTextField(15);
	        prodPriceEdit.add(pNewPrice);
	        JLabel warningEdit = new JLabel("");
	        warningEdit.setVisible(false);
	        JButton editProdBtn = new JButton("Done");
	        editProdBtn.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		Double pric;
	        		String s = pNewPrice.getText();
	        		if(s.equals("")) { //display warning - price required
	        			warningEdit.setText("New price required!");
	        			warningEdit.setVisible(true);
                		return;
                	}
                	pric = Double.parseDouble(s);
                	Department d = store.getDepWithItemID(itemToEdit.getID());
                	//modify in store
        			Item eModified = store.getItemWithID(itemToEdit.getID());
        			eModified.setPrice(pric);
        			itemToEdit.setPrice(pric);
        			//modify in vector on which modellist is based
        			for(int k = 0; k < itemVector.size(); k++) {
        				if(itemVector.get(k).getID() == itemToEdit.getID()) {
        					itemVector.get(k).setPrice(pric);
        					break;
        				}
        			}
        			//modify in carts
        			Vector custo = d.getCustomers();
	    			for(int j = 0; j < custo.size(); j++) {
	    				Customer cus = (Customer)custo.get(j);
	    				ShoppingCart sCart = cus.getCart();
	    				Item eToEdit = sCart.getItemWithID(itemToEdit.getID());
	    				if(eToEdit != null) {
	    					eToEdit.setPrice(pric);
	    				}
	    			}
	    			//notify observers
        			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        			Date date = new Date();
        			String sDate = dateFormat.format(date);
        			Notification n = new Notification(sDate, NotificationType.MODIFY, d.getID(), eModified.getID());
        			d.notifyAllObservers(n);
        			main.setVisible(true);
        			editProd.setVisible(false);
        			pNewPrice.setText("");
                	warningEdit.setVisible(false);
                	itemList.clearSelection(); //clear JList selection
                	itemToEdit = null;
        			show();
			        pack();
	            }
	        });
	        JButton cancelEditBtn = new JButton("Cancel");
	        //cancel product edit
	        cancelEditBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	main.setVisible(true);
                	editProd.setVisible(false);
                	pNewPrice.setText("");
                	warningEdit.setVisible(false);
                	itemList.clearSelection();
                	itemToEdit = null;
        			show();
			        pack();
                }
            });
	        prodPriceEdit.add(editProdBtn);
	        prodPriceEdit.add(cancelEditBtn);
	        editProd.add(prodPriceEdit);
	        editProd.add(warningEdit);
	        editProd.setVisible(false);
	        container.add(editProd);
	        
	        container.add(main);
	        add(container);
		}
		
		//renders cart panel
		public void renderCart() {
            cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS)); //boxlayout for vertical display
	        JLabel cartTitle = new JLabel("<html><h3><strong>Modify ShoppingCart</h3><hr></strong></html>");
	        cartTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
	        cartPanel.add(cartTitle);
			ShoppingCart sCart = customerToEdit.getCart();
        	ListIterator it = sCart.listIterator();
        	DefaultListModel<Item> cartItems = new DefaultListModel();
    		while(it.hasNext()) {
    			Item item = (Item)it.next();
    	        cartItems.addElement(item);
    		}
    		JList cartList = new JList(cartItems);
    		cartList.setCellRenderer(new ItemRenderer());
    		cartPanel.add(new JScrollPane(cartList));
    		JPanel cartButtons = new JPanel();
    		cartButtons.setLayout(new GridLayout(7,1));
        	addToCart.setLayout(new BoxLayout(addToCart, BoxLayout.Y_AXIS));
    		JButton cartAddBtn = new JButton("Add");
    		//adding a product to the cart requires another panel with store items
    		JLabel addToCartTitle = new JLabel("<html><h3><strong>Select a product:</h3></strong><hr></html>");
        	addToCartTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        	addToCart.add(addToCartTitle);
        	JList addToCartList = new JList(items);
        	addToCartList.setCellRenderer(new ItemRenderer());
        	addToCart.add(new JScrollPane(addToCartList));
        	JPanel addToCartButtons = new JPanel();
        	addToCartButtons.setLayout(new GridLayout(1,2));
        	JButton addToCartBtn = new JButton("Add");
        	addToCartButtons.add(addToCartBtn);
        	addToCartBtn.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		Item itemToAdd = (Item)addToCartList.getSelectedValue();
	        		if(itemToAdd == null) {
	        			return;
	        		}
	                Item eNewToAdd = new Item(itemToAdd.getName(), itemToAdd.getID(), itemToAdd.getPrice());
	                sCart.add(eNewToAdd);
	                cartItems.clear();
	                ListIterator it = sCart.listIterator();
	                while(it.hasNext()) {
	        			Item item = (Item)it.next();
	        	        cartItems.addElement(item);
	        		}
	                addToCartList.clearSelection();
					Department d = store.getDepWithItemID(itemToAdd.getID());
					d.enter(customerToEdit);
					//update total and budget labels with new values
					String pr = String.format(Locale.US, "%.2f", sCart.getBudget());
					budget.setText("Budget: " + pr);
					pr = String.format(Locale.US, "%.2f", sCart.getTotalPrice());
					total.setText("Total: " + pr);
					addToCart.setVisible(false);
					cartPanel.setVisible(true);
					itemToAdd = null;
					show();
					pack();
	            }
	        });
        	
        	JButton cancelAddToCartBtn = new JButton("Cancel");
        	cancelAddToCartBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	addToCart.setVisible(false);
                	cartPanel.setVisible(true);
                	cartList.clearSelection();
                	addToCartList.clearSelection();
        			show();
			        pack();
                }
            });
        	addToCartButtons.add(cancelAddToCartBtn);
        	addToCart.add(addToCartButtons);
        	
    		cartAddBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	addToCart.setVisible(true);
                	cartPanel.setVisible(false);
                	cartList.clearSelection();
        			show();
			        pack();
                }
            });
    		cartButtons.add(cartAddBtn);
    		
    		//apply strategy button
    		JButton cartStrategyBtn = new JButton("Apply Strategy");
    		cartStrategyBtn.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		Item toTest = (Item)customerToEdit.getWishes().getItem(0);
	        		if(toTest != null && toTest.getPrice() <= sCart.getBudget()) {
						Item eFromStrategy = customerToEdit.getWishes().executeStrategy();
						Department d = store.getDepWithItemID(eFromStrategy.getID());
						sCart.add(eFromStrategy);
						d.enter(customerToEdit);
						Vector depItems = d.getItems();
						boolean stillObserver = false;
						for(int k = 0; k < depItems.size(); k++) {
							Item item = (Item)depItems.get(k);
							Item eToVerify = customerToEdit.getWishes().getItemWithID(item.getID());
							if(eToVerify != null) {
								stillObserver = true;
							}
						}
						if(stillObserver == false) {
							d.removeObserver(customerToEdit);
						}
						cartItems.clear();
		                ListIterator it = sCart.listIterator();
		                while(it.hasNext()) {
		        			Item item = (Item)it.next();
		        	        cartItems.addElement(item);
		        		}
		                String pr = String.format(Locale.US, "%.2f", sCart.getBudget());
						budget.setText("Budget: " + pr);
						pr = String.format(Locale.US, "%.2f", sCart.getTotalPrice());
						total.setText("Total: " + pr);
						show();
				        pack();
					}
	            }
	        });
    		cartButtons.add(cartStrategyBtn);
    		
    		JButton cartRemoveBtn = new JButton("Remove");
    		//remove product from cart
    		cartRemoveBtn.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	                ListSelectionModel selItem = cartList.getSelectionModel();
	                Item item = (Item)cartList.getSelectedValue();
	                int i = selItem.getMinSelectionIndex();
	                if (i >= 0) {
	                	cartItems.remove(i);
	                	sCart.remove(i);
	                	String pr = String.format(Locale.US, "%.2f", sCart.getBudget());
						budget.setText("Budget: " + pr);
						pr = String.format(Locale.US, "%.2f", sCart.getTotalPrice());
						total.setText("Total: " + pr);
						show();
				        pack();
	                }
	            }
	        });
    		cartButtons.add(cartRemoveBtn);
    		
    		JButton cartOrderBtn = new JButton("Place order");
    		//place order; creates new shopping cart with remaining budget
    		cartOrderBtn.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	                double newBudget = sCart.getBudget();
	                ShoppingCart sNew = new ShoppingCart(new CompCartAndA(), newBudget);
	                customerToEdit.setCart(sNew);
	                cartPanel.removeAll();
	                addToCart.removeAll();
	                renderCart();
	                show();
			        pack();
	            }
	        });
    		cartButtons.add(cartOrderBtn);
    		
    		JButton cartMenuBtn = new JButton("Main Page");
    		//returns to main page of store
    		cartMenuBtn.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	                cartPanel.removeAll();
	                addToCart.removeAll();
	                cartPanel.setVisible(false);
	                main.setVisible(true);
	                customerToEdit = null;
	                customerList.clearSelection();
	                itemList.clearSelection();
	                show();
			        pack();
	            }
	        });
    		cartButtons.add(cartMenuBtn);
    		
    		//display total price and available budget
    		String pr = String.format(Locale.US, "%.2f", sCart.getBudget());
    		budget = new JLabel("Budget: " + pr, SwingConstants.CENTER);
    		cartButtons.add(budget);
    		pr = String.format(Locale.US, "%.2f", sCart.getTotalPrice());
    		total = new JLabel("Total: " + pr, SwingConstants.CENTER);
    		cartButtons.add(total);
    		cartPanel.add(cartButtons);
		}
		
		//renders wishlist panel; similar to cart panel
		public void renderWish() {
			wishPanel.setLayout(new BoxLayout(wishPanel, BoxLayout.Y_AXIS));
	        JLabel wishTitle = new JLabel("<html><h3><strong>Modify WishList</h3><hr></strong></html>");
	        wishTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
	        wishPanel.add(wishTitle);
			WishList wList = customerToEdit.getWishes();
        	ListIterator it = wList.listIterator();
        	DefaultListModel<Item> wishItems = new DefaultListModel();
    		while(it.hasNext()) {
    			Item item = (Item)it.next();
    	        wishItems.addElement(item);
    		}
    		JList wishList = new JList(wishItems);
    		wishList.setCellRenderer(new ItemRenderer());
    		wishPanel.add(new JScrollPane(wishList));
    		JPanel wishButtons = new JPanel();
    		wishButtons.setLayout(new GridLayout(3,1));
        	addToWish.setLayout(new BoxLayout(addToWish, BoxLayout.Y_AXIS));
    		JButton wishAddBtn = new JButton("Add");
    		JLabel addToWishTitle = new JLabel("<html><h3><strong>Select a product:</h3></strong><hr></html>");
        	addToWishTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        	addToWish.add(addToWishTitle);
        	JList addToWishList = new JList(items);
        	addToWishList.setCellRenderer(new ItemRenderer());
        	addToWish.add(new JScrollPane(addToWishList));
        	JPanel addToWishButtons = new JPanel();
        	addToWishButtons.setLayout(new GridLayout(1,2));
        	JButton addToWishBtn = new JButton("Add");
        	addToWishButtons.add(addToWishBtn);
        	addToWishBtn.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		Item itemToAdd = (Item)addToWishList.getSelectedValue();
	        		if(itemToAdd == null) {
	        			return;
	        		}
	                Item eNewToAdd = new Item(itemToAdd.getName(), itemToAdd.getID(), itemToAdd.getPrice());
	                wList.add(eNewToAdd);
	                wishItems.clear();
	                ListIterator it = wList.listIterator();
	                while(it.hasNext()) {
	        			Item item = (Item)it.next();
	        	        wishItems.addElement(item);
	        		}
	                addToWishList.clearSelection();
	                Department d = store.getDepWithItemID(itemToAdd.getID());
					d.addObserver(customerToEdit);
					addToWish.setVisible(false);
					wishPanel.setVisible(true);
					itemToAdd = null;
					show();
					pack();
	            }
	        });
        	JButton cancelAddToWishBtn = new JButton("Cancel");
        	cancelAddToWishBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	addToWish.setVisible(false);
                	wishPanel.setVisible(true);
                	wishList.clearSelection();
                	addToWishList.clearSelection();
        			show();
			        pack();
                }
            });
        	addToWishButtons.add(cancelAddToWishBtn);
        	addToWish.add(addToWishButtons);
    		wishAddBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	addToWish.setVisible(true);
                	wishPanel.setVisible(false);
                	wishList.clearSelection();
        			show();
			        pack();
                }
            });
    		wishButtons.add(wishAddBtn);
    		JButton wishRemoveBtn = new JButton("Remove");
    		wishRemoveBtn.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	                ListSelectionModel selItem = wishList.getSelectionModel();
	                Item item = (Item)wishList.getSelectedValue();
	                int i = selItem.getMinSelectionIndex();
	                if (i >= 0) {
	                	wishItems.remove(i);
	                	wList.remove(i);
	                	Department d = store.getDepWithItemID(item.getID());
						Vector depItems = d.getItems();
						boolean stillObserver = false;
						for(int k = 0; k < depItems.size(); k++) {
							Item depItem = (Item)depItems.get(k);
							Item eTest = wList.getItemWithID(depItem.getID());
							if(eTest != null) {
								stillObserver = true;
							}
						}
						if(stillObserver == false) {
							d.removeObserver(customerToEdit);
						}
						show();
				        pack();
	                }
	            }
	        });
    		wishButtons.add(wishRemoveBtn);
    		JButton wishMenuBtn = new JButton("Main Page");
    		wishMenuBtn.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		wishPanel.removeAll();
	                addToWish.removeAll();
	                wishPanel.setVisible(false);
	                main.setVisible(true);
	                customerToEdit = null;
	                customerList.clearSelection();
	                itemList.clearSelection();
	                show();
			        pack();
	            }
	        });
    		wishButtons.add(wishMenuBtn);
    		wishPanel.add(wishButtons);
		}
		
		//renders notifications panel for selected customer
		public void renderNotif() {
			notifPanel.setLayout(new BoxLayout(notifPanel, BoxLayout.Y_AXIS));
	        JLabel notifTitle = new JLabel("<html><h3><strong>These are your notifications:</h3><hr></strong></html>");
	        notifTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
	        notifPanel.add(notifTitle);
			Vector<Notification> notifications = customerToEdit.getNotifications();
        	DefaultListModel<String> notifItems = new DefaultListModel();
    		for(int i = 0; i < notifications.size(); i++) {
    			Notification n = notifications.get(i);
    	        notifItems.addElement(n.toStringForInterface());
    		}
    		JList notifList = new JList(notifItems);
    		notifPanel.add(new JScrollPane(notifList));
    		JButton mainPageBtn = new JButton("Main Page");
    		mainPageBtn.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		notifPanel.removeAll();
	                notifPanel.setVisible(false);
	                main.setVisible(true);
	                customerToEdit = null;
	                customerList.clearSelection();
	                itemList.clearSelection();
	                show();
			        pack();
	            }
	        });
    		notifPanel.add(mainPageBtn);
		}
		
		//renders each department panel
		public void renderDep() {
			depPanel.setLayout(new BoxLayout(depPanel, BoxLayout.Y_AXIS));
			String depName = depToShow.getName();
	        JLabel depTitle = new JLabel(depName);
	        //using fonts to customize look as HTML is not available for String
	        depTitle.setFont(new Font("Dialog", Font.BOLD, 18));
	        depTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
	        depPanel.add(depTitle);
			Vector<Customer> customers = depToShow.getCustomers();
        	DefaultListModel<String> custoItems = new DefaultListModel();
    		for(int i = 0; i < customers.size(); i++) {
    			String s = customers.get(i).getName();
    	        custoItems.addElement(s);
    		}
    		Vector<Customer> observers = depToShow.getVectorObservers();
        	DefaultListModel<String> obsItems = new DefaultListModel();
    		for(int i = 0; i < observers.size(); i++) {
    			String s = observers.get(i).toString();
    			obsItems.addElement(s);
    		}
    		JPanel depListPanel = new JPanel();
    		JPanel depTitlesPanel = new JPanel();
    		JList custoList = new JList(custoItems);
    		JList obsList = new JList(obsItems);
    		depListPanel.setLayout(new GridLayout(1,2));
    		depTitlesPanel.setLayout(new GridLayout(1,2));
    		depListPanel.add(new JScrollPane(custoList));
    		depListPanel.add(new JScrollPane(obsList));
    		JLabel custoTitle = new JLabel("Customers:");
    		JLabel obsTitle = new JLabel("Observers:");
    		depTitlesPanel.add(custoTitle);
    		depTitlesPanel.add(obsTitle);
    		depPanel.add(depTitlesPanel);
    		depPanel.add(depListPanel);
    		//setting "most" labels initial value
    		JLabel mostBought = new JLabel("Most bought: Not yet available");
    		JLabel mostWanted = new JLabel("Most wanted: Not yet available");
    		JLabel mostExpensive = new JLabel("Most expensive: Not yet available");
    		Item e = depToShow.mostBought();
    		//getting "most" products, if available
    		if(e != null) {
    			String s = e.toString();
        		mostBought.setText("Most bought: " + s);
    		}
    		e = depToShow.mostWanted();
    		if(e != null) {
    			String s = e.toString();
    			mostWanted.setText("Most wanted: " + s);
    		}
    		e = depToShow.mostExpensive();
    		if(e != null) {
    			String s = e.toString();
    			mostExpensive.setText("Most expensive: " + s);
    		}
    		depPanel.add(mostBought);
    		depPanel.add(mostWanted);
    		depPanel.add(mostExpensive);
    		
    		JButton mainPageBtn = new JButton("Main Page");
    		//returns to main store page
    		mainPageBtn.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		depPanel.removeAll();
	                depPanel.setVisible(false);
	                main.setVisible(true);
	                depToShow = null;
	                customerList.clearSelection();
	                itemList.clearSelection();
	                show();
			        pack();
	            }
	        });
    		depPanel.add(mainPageBtn);
		}
		
	}
	
	//listcellrenderer for items JLists
	public class ItemRenderer extends JPanel implements ListCellRenderer<Item> {
		private JLabel ID = new JLabel();
		private JLabel name = new JLabel();
		private JLabel price = new JLabel();
		private JLabel department = new JLabel();
		
		ItemRenderer() {
			setLayout(new BorderLayout(5,5));
			JPanel panel = new JPanel(new GridLayout(0, 1));
			panel.add(ID);
			panel.add(name);
			panel.add(price);
			panel.add(department);
			add(panel, BorderLayout.CENTER);
		}
		
		public Component getListCellRendererComponent(JList<? extends Item> list, Item item, int index, boolean isSelected, boolean cellHasFocus) {
			ID.setText("ID: " + String.valueOf(item.getID()));
			name.setText("Name: " + item.getName());
			price.setText("Price: " + String.valueOf(item.getPrice()));
			department.setText("Department: " + ((Department)store.getDepWithItemID(item.getID())).getName());
			
			ID.setOpaque(true);
			name.setOpaque(true);
			price.setOpaque(true);
			department.setOpaque(true);
			
			name.setForeground(Color.blue);
			//varying backgrounds for better clarity
			if(isSelected == true) {
				ID.setBackground(Color.red);
	        	name.setBackground(Color.red);
	        	price.setBackground(Color.red);
	        	department.setBackground(Color.red);
	            setBackground(Color.red);
	        } else if(index % 2 == 0) {
	        	ID.setBackground(Color.green);
	        	name.setBackground(Color.green);
	        	price.setBackground(Color.green);
	        	department.setBackground(Color.green);
	            setBackground(Color.green);
	        } else {
	        	ID.setBackground(Color.lightGray);
	        	name.setBackground(Color.lightGray);
	        	price.setBackground(Color.lightGray);
	        	department.setBackground(Color.lightGray);
	            setBackground(Color.lightGray);
	        }
			return this;
		}
	}

	//main function
	public static void main(String[] args) {
		TestInterface start = new TestInterface();
	}
}