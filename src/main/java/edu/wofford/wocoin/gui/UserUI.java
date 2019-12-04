package edu.wofford.wocoin.gui;

import edu.wofford.wocoin.GUIController;
import edu.wofford.wocoin.Product;
import edu.wofford.wocoin.WalletUtilities.CreateWalletResult;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

public class UserUI extends JPanel implements ActionListener {
    private GUIController gc;
    private CardLayout loginScreenLayout;

	private JTextField txtUsername;
	private JPasswordField txtPassword;
	
	private UserRootMenu userRootMenu;

    public UserUI(GUIController gc) {
        this.gc = gc;
        loginScreenLayout = new CardLayout();
		
		setLayout(loginScreenLayout);
		
		JPanel pnlLogin = new JPanel();
		setupLoginPanel(pnlLogin);
		this.add(pnlLogin, "login screen");
		
		userRootMenu = new UserRootMenu();
		this.add(userRootMenu, "user control screen");
    }

    public void setupLoginPanel(JPanel pnlLogin) {
    	pnlLogin.setLayout(new FlowLayout());
		
		txtUsername = new JTextField(20);
		txtPassword = new JPasswordField(20);
		pnlLogin.add(new JLabel("Username: "));
		pnlLogin.add(txtUsername);
		pnlLogin.add(new JLabel("Password: "));
		pnlLogin.add(txtPassword);
		
		JButton loginButton = new JButton("Login");
		loginButton.setActionCommand("login");
		loginButton.addActionListener(this);
		pnlLogin.add(loginButton);
    }

    public void logout() {
    	gc.doLogout();
    	this.txtUsername.setText("");
	    this.txtPassword.setText("");
    	this.loginScreenLayout.show(this, "login screen");
    }
    @Override
	public void actionPerformed(ActionEvent e) {
		txtUsername.setBackground(Color.WHITE);
		txtPassword.setBackground(Color.WHITE);
		if (gc.userLogin(txtUsername.getText(), new String(txtPassword.getPassword()))) {
			this.loginScreenLayout.show(this, "user control screen");
			userRootMenu.showRootMenu();
		}
		else {
			txtUsername.setBackground(Color.RED);
			txtPassword.setBackground(Color.RED);
		}
	}

    private class UserRootMenu extends JPanel implements ActionListener {
    	private JPanel userOptionsMenu;
	    private CardLayout rootMenuLayout;
    	private HashMap<String, UserActionPanel> actionPanels;
    	
        public UserRootMenu() {
        	rootMenuLayout = new CardLayout();
        	actionPanels = new HashMap<String, UserActionPanel>();
        	this.setLayout(rootMenuLayout);
        	
        	userOptionsMenu = new JPanel();
        	reloadUserMenu();
        	this.add(userOptionsMenu, "user options menu");
        	
        	CreateWalletPanel createWalletPanel = new CreateWalletPanel(this);
        	this.add(createWalletPanel, "Create a Wallet");

        	CreateProductPanel createProductPanel = new CreateProductPanel(this);
        	this.add(createProductPanel, "Add a Product");

        	RemoveProductPanel removeProductPanel = new RemoveProductPanel(this);
        	this.add(removeProductPanel, "Remove a Product");

	        DisplayProductsPanel displayProductsPanel = new DisplayProductsPanel(this);
	        this.add(displayProductsPanel, "Display Products");

	        SendMessagePanel sendMessagePanel = new SendMessagePanel(this);
	        this.add(sendMessagePanel, "Send a Message");

        	actionPanels.put("Create a Wallet", createWalletPanel);
        	actionPanels.put("Remove a Product", removeProductPanel);
        	actionPanels.put("Display Products", displayProductsPanel);
        	actionPanels.put("Send a Message", sendMessagePanel);
        }
        
        public void reloadUserMenu() {
        	userOptionsMenu.removeAll();
        	String[] buttonNames;
        	if (!gc.userHasWallet()) {
        		buttonNames = new String[]{"Create a Wallet"};
        	} else {
		        JLabel balanceLabel = new JLabel(gc.getUserBalance());
        		userOptionsMenu.add(balanceLabel);
        		buttonNames = new String[] {"Create a Wallet", "Add a Product", "Remove a Product", "Display Products",
        			"Send a Message", "Get User Messages", "Buy a Product"
        		};
        	}

        	JButton newButton;
        	for (String buttonName : buttonNames) {
        		newButton = new JButton(buttonName);
        		newButton.addActionListener(this);
        		newButton.setAlignmentX(CENTER_ALIGNMENT);
        		userOptionsMenu.add(newButton);
        	}
        }
        
        protected void showRootMenu() {
        	reloadUserMenu();
        	rootMenuLayout.show(this, "user options menu");
        }
        
        @Override
    	public void actionPerformed(ActionEvent e) {
    		rootMenuLayout.show(this, e.getActionCommand());
    		UserActionPanel actionPanel = actionPanels.get(e.getActionCommand());
    		if (actionPanel != null) {
    			actionPanel.showPanel();
    		}
    	}

        private class CreateWalletPanel extends JPanel implements UserActionPanel, ActionListener {
        	UserRootMenu parentPanel;
        	JFileChooser fileChooser;
        	
        	public CreateWalletPanel(UserRootMenu parentPanel) {
        		this.parentPanel = parentPanel;
				JButton backButton = new JButton("Back");
				backButton.addActionListener(this);
				this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				backButton.setAlignmentX(LEFT_ALIGNMENT);
				
				this.add(backButton);
				this.add(Box.createVerticalStrut(15));
			}
        	
			public void showPanel() {
                boolean userStillCreatingWallet = true;
        		if (gc.userHasWallet()) {
        			userStillCreatingWallet = JOptionPane.showConfirmDialog(null, "Would you like to add a new wallet?",
					        "Your account has an associated wallet.", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        			
        			if (!userStillCreatingWallet) {
        				parentPanel.showRootMenu();
        			}
        		}
        		
        		if (userStillCreatingWallet) {
        			fileChooser = new JFileChooser();
			        fileChooser.setCurrentDirectory(new java.io.File("."));
			        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			        fileChooser.setAcceptAllFileFilterUsed(false);
        			fileChooser.setAlignmentX(LEFT_ALIGNMENT);

        			this.add(fileChooser);
        			JButton selectDirectoryButton = new JButton("Create Wallet");
        			selectDirectoryButton.addActionListener(this);
        			selectDirectoryButton.setAlignmentX(LEFT_ALIGNMENT);
        			this.add(selectDirectoryButton);
        			this.updateUI();
        		}
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String actionName = e.getActionCommand();
				if (actionName.equals("Back")) {
					parentPanel.showRootMenu();
				}
				else if (actionName.equals("Create Wallet")) {
					boolean result = gc.addWalletToUser(fileChooser.getCurrentDirectory().toString()) == CreateWalletResult.SUCCESS;
					if (result) {
						JOptionPane.showMessageDialog(null, "Wallet created successfully");
						this.parentPanel.showRootMenu();
					}
					else {
						JOptionPane.showMessageDialog(null, "An error occurred, try again");
					}
				}
			}
        }

        private class CreateProductPanel extends JPanel implements ActionListener {

	        private final UserRootMenu parentPanel;
	        private JTextField txtItemName;
	        private JTextArea txtItemDescription;
	        private JSpinner txtItemPrice;

	        public CreateProductPanel(UserRootMenu parentPanel) {
		        this.parentPanel = parentPanel;

		        JButton backButton = new JButton("Back");
		        backButton.addActionListener(this);

		        this.setLayout(new GridBagLayout());
		        GridBagConstraints gc = new GridBagConstraints();
		        gc.fill = GridBagConstraints.HORIZONTAL;
		        gc.insets = new Insets(10, 10, 10, 10);

		        gc.gridx = 0;
		        gc.gridy = 0;
		        gc.anchor = GridBagConstraints.PAGE_START;
		        this.add(backButton, gc);

		        JLabel itemNameLabel = new JLabel("Name", JLabel.TRAILING);
		        txtItemName = new JTextField();
		        itemNameLabel.setLabelFor(txtItemName);

		        JLabel itemDescriptionLabel = new JLabel("Description", JLabel.TRAILING);
		        txtItemDescription = new JTextArea();
		        itemDescriptionLabel.setLabelFor(txtItemDescription);

		        JLabel itemPriceLabel = new JLabel("Price", JLabel.TRAILING);
		        txtItemPrice = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		        itemPriceLabel.setLabelFor(txtItemPrice);

		        JButton addProduct = new JButton("Add Product");
		        addProduct.addActionListener(this);

		        gc.gridy = 1;
		        this.add(itemNameLabel, gc);
		        gc.gridy = 2;
		        this.add(itemDescriptionLabel, gc);
		        gc.gridy = 3;
		        this.add(itemPriceLabel, gc);

		        gc.gridx = 1;

		        gc.gridy = 1;
		        this.add(txtItemName, gc);
		        gc.gridy = 2;
		        this.add(txtItemDescription, gc);
		        gc.gridy = 3;
		        this.add(txtItemPrice, gc);

		        gc.gridx = 0;
		        gc.gridy = 4;
		        this.add(addProduct, gc);
	        }

	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	String actionName = e.getActionCommand();
				if (actionName.equals("Back")) {
					this.parentPanel.showRootMenu();
				}
				else {
					boolean errorOccurred = false;
					if (txtItemName.getText().length() == 0) {
						txtItemName.setBackground(Color.RED);
						txtItemName.setToolTipText("Enter at least one character");
						errorOccurred = true;
					}
					else {
						txtItemName.setBackground(Color.WHITE);
						txtItemName.setToolTipText("");
					}

					if (txtItemDescription.getText().length() == 0) {
						txtItemDescription.setBackground(Color.RED);
						txtItemDescription.setToolTipText("Enter at least one character");
						errorOccurred = true;
					}
					else {
						txtItemDescription.setBackground(Color.WHITE);
						txtItemDescription.setToolTipText("");
					}

					if (!errorOccurred) {
						String output = gc.addNewProduct(txtItemName.getText(), txtItemDescription.getText(), (Integer) txtItemPrice.getValue());
						JOptionPane.showMessageDialog(null, output);
						if (output.equals("Product added.")) {
							txtItemName.setText("");
							txtItemDescription.setText("");
							txtItemPrice.setValue(1);
							this.parentPanel.showRootMenu();
						}
					}
				}
	        }
        }

        private class RemoveProductPanel extends JPanel implements UserActionPanel, ActionListener {
	        private final UserRootMenu parentPanel;

	        private JList<Product> productJList;
	        private DefaultListModel<Product> listModel;

	        public RemoveProductPanel(UserRootMenu parentPanel) {
		        this.parentPanel = parentPanel;

		        JButton backButton = new JButton("Back");
		        backButton.addActionListener(this);

		        this.setLayout(new GridBagLayout());
		        GridBagConstraints gridBagConstraints = new GridBagConstraints();
		        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		        gridBagConstraints.insets = new Insets(10, 10, 10, 10);

		        gridBagConstraints.gridx = 0;
		        gridBagConstraints.gridy = 0;
		        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
		        this.add(backButton, gridBagConstraints);

		        ArrayList<Product> products = gc.getUserProducts();

		        listModel = new DefaultListModel<>();
		        listModel.addAll(products);

		        productJList = new JList<>(listModel);
		        productJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		        productJList.setSelectedIndex(0);
		        productJList.setVisibleRowCount(8);
		        JScrollPane listScrollPane = new JScrollPane(productJList);

		        gridBagConstraints.gridy = 1;
		        gridBagConstraints.anchor = GridBagConstraints.CENTER;
		        this.add(listScrollPane, gridBagConstraints);

		        JButton removeProductButton = new JButton("Remove Selected Product");
		        removeProductButton.addActionListener(this);
		        gridBagConstraints.gridy = 2;
		        this.add(removeProductButton, gridBagConstraints);
	        }

	        @Override
	        public void actionPerformed(ActionEvent e) {
				String actionName = e.getActionCommand();
				if (actionName.equals("Back")) {
					parentPanel.showRootMenu();
				}
				else {
					Product productToRemove = productJList.getSelectedValue();
					String removeProductResult = gc.removeProduct(productToRemove);
					JOptionPane.showMessageDialog(null, removeProductResult);
					if (removeProductResult.equals("Product removed.")) {
						parentPanel.showRootMenu();
					}

				}
	        }

	        @Override
	        public void showPanel() {
		        ArrayList<Product> products = gc.getUserProducts();
		        listModel = new DefaultListModel<>();
		        listModel.addAll(products);
		        productJList.setModel(listModel);
		        productJList.updateUI();
		        this.updateUI();
	        }
        }

        private class DisplayProductsPanel extends JPanel implements UserActionPanel, ActionListener {
        	private final UserRootMenu parentPanel;

	        private JList<Product> productJList;
	        private DefaultListModel<Product> listModel;

	        public DisplayProductsPanel(UserRootMenu parentPanel) {
		        this.parentPanel = parentPanel;

		        JButton backButton = new JButton("Back");
		        backButton.addActionListener(this);

		        this.setLayout(new GridBagLayout());
		        GridBagConstraints gridBagConstraints = new GridBagConstraints();
		        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		        gridBagConstraints.insets = new Insets(10, 10, 10, 10);

		        gridBagConstraints.gridx = 0;
		        gridBagConstraints.gridy = 0;
		        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
		        this.add(backButton, gridBagConstraints);

		        ArrayList<Product> products = gc.getUserProducts();

		        listModel = new DefaultListModel<>();
		        listModel.addAll(products);

		        productJList = new JList<>(listModel);
		        productJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		        productJList.setSelectedIndex(0);
		        productJList.setVisibleRowCount(8);
		        JScrollPane listScrollPane = new JScrollPane(productJList);

		        gridBagConstraints.gridy = 1;
		        gridBagConstraints.anchor = GridBagConstraints.CENTER;
		        this.add(listScrollPane, gridBagConstraints);
	        }

	        @Override
	        public void actionPerformed(ActionEvent e) {
				parentPanel.showRootMenu();
	        }

	        @Override
	        public void showPanel() {
		        ArrayList<Product> products = gc.getAllProducts();
		        products.sort(Product::compareToWithPrice);

		        products.forEach(product -> {
			        product.setCurrentUser(gc.getCurrentUser());
			        product.setDisplayType(Product.DisplayType.SHOWCURRENTUSER);
		        });

		        listModel = new DefaultListModel<>();
		        listModel.addAll(products);
		        productJList.setModel(listModel);
		        productJList.updateUI();
		        this.updateUI();
	        }
        }

        private class SendMessagePanel extends JPanel implements UserActionPanel, ActionListener {
        	private final UserRootMenu parentPanel;

			private JList<Product> productJList;

	        private JTextArea txtMessageArea;

	        public SendMessagePanel(UserRootMenu parentPanel) {
		        this.parentPanel = parentPanel;
		        JButton backButton = new JButton("Back");
		        backButton.addActionListener(this);

		        this.setLayout(new GridBagLayout());
		        GridBagConstraints gridBagConstraints = new GridBagConstraints();
		        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		        gridBagConstraints.insets = new Insets(10, 10, 10, 10);

		        gridBagConstraints.gridx = 0;
		        gridBagConstraints.gridy = 0;
		        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
		        this.add(backButton, gridBagConstraints);

		        ArrayList<Product> products = getProductsList();
		        DefaultListModel<Product> listModel = new DefaultListModel<>();
		        listModel.addAll(products);

		        productJList = new JList<>(listModel);
		        productJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		        productJList.setSelectedIndex(0);
		        productJList.setVisibleRowCount(8);
		        JScrollPane listScrollPane = new JScrollPane(productJList);

		        gridBagConstraints.gridwidth = 2;
		        gridBagConstraints.gridy = 1;
		        gridBagConstraints.anchor = GridBagConstraints.CENTER;
		        this.add(listScrollPane, gridBagConstraints);

		        gridBagConstraints.gridx = 0;
		        gridBagConstraints.gridy = 2;
		        gridBagConstraints.gridwidth = 1;
				JLabel messageLabel = new JLabel("What is your message?");
				this.add(messageLabel, gridBagConstraints);

				gridBagConstraints.gridx = 1;
		        this.txtMessageArea = new JTextArea();

		        this.add(txtMessageArea, gridBagConstraints);

		        JButton btnSendMessage = new JButton("Send");
		        btnSendMessage.addActionListener(this);
		        gridBagConstraints.gridwidth = 2;
		        gridBagConstraints.gridx = 0;
		        gridBagConstraints.gridy = 3;
		        this.add(btnSendMessage, gridBagConstraints);
	        }

	        @Override
	        public void showPanel() {
				ArrayList<Product> products = getProductsList();
		        DefaultListModel<Product> listModel = new DefaultListModel<>();
		        listModel.addAll(products);
		        productJList.setModel(listModel);
		        productJList.setSelectedIndex(0);
		        productJList.updateUI();
		        this.updateUI();
	        }

	        private ArrayList<Product> getProductsList() {
		        ArrayList<Product> products = gc.getPurchasableProducts(false);
		        products.sort(Product::compareToWithPrice);
		        return products;
	        }

	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	txtMessageArea.setBackground(Color.WHITE);

		        String actionName = e.getActionCommand();
		        if (actionName.equals("Back")) {
			        parentPanel.showRootMenu();
		        }
		        else if (txtMessageArea.getText().length() == 0) {
					txtMessageArea.setBackground(Color.RED);
		        }
		        else {
			        Product productInMessage = productJList.getSelectedValue();
			        String sendMessageResult = gc.sendMessage(productInMessage, txtMessageArea.getText());
			        JOptionPane.showMessageDialog(null, sendMessageResult);
			        if (sendMessageResult.equals("Message sent.")) {
				        parentPanel.showRootMenu();
			        }
		        }
	        }
        }
        	/*
            public SendMessageAction(ViewConfig viewConfig, Scanner keyboard) {
                super("Pick a Product to send a message to its seller", "send message", viewConfig, keyboard);
	            this.viewConfig = new ViewConfig.Builder()
									            .setBackMenuName("cancel")
									            .setIndexNumberFormatter(index -> (index + 1) + ": ")
									            .build();
            }

            @Override
            public void executeCustomAction() {
            	if (!gc.userHasWallet()) {
            		this.println("User has no wallet.");
	            }
            	else {
		            ArrayList<Product> products = gc.getPurchasableProducts(false);
		            products.sort(Product::compareToWithPrice);

		            this.println("1: cancel");
		            for (int i = 0; i < products.size(); i++) {
			            this.println(String.format("%d: %s", i + 2, products.get(i).toString()));
		            }

		            int selected = this.prompt("Which product number is the subject of the message? ", Integer.class);

		            if (selected == 1) {
			            this.println("Action canceled.");
		            } else if (!gc.userHasWallet()) {
			            this.println("User has no wallet.");
		            } else if (selected < 1 || selected - 1 > products.size()) {
			            this.println(String.format("Invalid value. Enter a value between 1 and %d.", products.size() + 1));
			            this.println("Action canceled.");
		            } else {
			            String userMessage = this.prompt("What is the message? ", String.class);
			            this.println(gc.sendMessage(products.get(selected - 2), userMessage));
		            }
	            }
            }
        }

        private class GetMessagesAction extends CustomActionView {
	        public GetMessagesAction(ViewConfig viewConfig, Scanner keyboard) {
	        	super("Pick a message to reply to or delete.", "check messages", viewConfig, keyboard);
	        }

	        @Override
	        public void executeCustomAction() {
				ArrayList<Message> messages = gc.getUserMessages();

		        this.println("1: cancel");
		        for (int i = 0; i < messages.size(); i++) {
			        this.println(String.format("%d: %s", i + 2, messages.get(i).toString()));
		        }

		        int selected = 0;
		        boolean validInput = false;

		        while (!validInput) {
			        selected = this.prompt("Which message? ", Integer.class);
			        validInput = selected >= 1 && selected - 1 <= messages.size();
			        if (!validInput){
				        this.println(String.format("Invalid value. Enter a value between 1 and %d.", messages.size() + 1));
			        }
		        }

		        if (selected == 1) {
			        this.println("Action canceled.");
		        }
		        else if (!gc.userHasWallet()) {
			        this.println("User has no wallet.");
		        }
		        else {
			        this.println("1: cancel");
			        this.println("2: reply");
			        this.println("3: delete");
			        int messageOption = this.prompt("What do you want to do? ", Integer.class);
			        if (messageOption == 1) {
			        	this.println("Action canceled.");
			        }
			        else if (messageOption == 2) {
			        	String reply = this.prompt("What would you like to reply? ", String.class);
						this.println(gc.replyToMessage(messages.get(selected - 2), reply));
			        }
			        else if (messageOption == 3) {
						this.println(gc.deleteMessage(messages.get(selected - 2)));
			        }
		        }
		        this.goBack();
	        }
        }

        private class GetBalanceAction extends CustomActionView {

	        public GetBalanceAction(ViewConfig viewConfig, Scanner keyboard) {
		        super("Current WoCoin Balance", "check balance", viewConfig, keyboard);
	        }

	        @Override
	        public void executeCustomAction() {
				this.println(gc.getUserBalance());
	        }
        }

        private class BuyProductAction extends CustomActionView {

	        public BuyProductAction(ViewConfig viewConfig, Scanner keyboard) {
		        super("Pick a product to buy", "purchase product", viewConfig, keyboard);
	        }

	        @Override
	        public void executeCustomAction() {
	        	if (gc.userHasWallet()) {
	        		String walletPath = this.prompt("What is the directory to your wallet? ", String.class);
					if (!gc.walletInDBMatchesGivenPath(walletPath)) {
							this.println("Invalid wallet.");
					}
					else {
						ArrayList<Product> products = gc.getPurchasableProducts(true);
						products.sort(Product::compareToWithPrice);

						this.println("1: cancel");
						for (int i = 0; i < products.size(); i++) {
							this.println(String.format("%d: %s", i + 2, products.get(i).toString()));
						}

						int selected = this.prompt("Which product would you like to buy? ", Integer.class);

						if (selected == 1) {
							this.println("Action canceled.");
						} else if (selected < 1 || selected - 1 > products.size()) {
							this.println(String.format("Invalid value. Enter a value between 1 and %d.", products.size() + 1));
							this.println("Action canceled.");
						} else {
							this.println(gc.buyProduct(walletPath, products.get(selected - 2)));
						}
					}
		        }
	        	else {
			        this.println("User has no wallet.");
		        }
	        }
        }*/
    }
}
