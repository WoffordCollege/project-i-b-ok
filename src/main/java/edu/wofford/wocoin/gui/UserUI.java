package edu.wofford.wocoin.gui;

import edu.wofford.wocoin.GUIController;
import edu.wofford.wocoin.Message;
import edu.wofford.wocoin.Product;
import edu.wofford.wocoin.WalletUtilities.CreateWalletResult;
import org.apache.batik.util.gui.LanguageDialog;

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
		if (e.getActionCommand().equals("Logout")) {
			this.loginScreenLayout.show(this, "login screen");
			userRootMenu.showRootMenu();
		}
		else {
			txtUsername.setBackground(Color.WHITE);
			txtPassword.setBackground(Color.WHITE);
			if (gc.userLogin(txtUsername.getText(), new String(txtPassword.getPassword()))) {
				this.loginScreenLayout.show(this, "user control screen");
				userRootMenu.showRootMenu();
				txtUsername.setText("");
				txtPassword.setText("");
			} else {
				txtUsername.setBackground(Color.RED);
				txtPassword.setBackground(Color.RED);
			}
		}
	}

	private class UserRootMenu extends JPanel implements ActionListener {
		private JPanel userOptionsMenu;
		private CardLayout rootMenuLayout;
		private HashMap<String, UserActionPanel> actionPanels;

		public UserRootMenu() {
			rootMenuLayout = new CardLayout();
			actionPanels = new HashMap<>();
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

			ShowMessagesPanel showMessagesPanel = new ShowMessagesPanel(this);
			this.add(showMessagesPanel, "Check Messages");

			BuyProductPanel buyProductPanel = new BuyProductPanel(this);
			this.add(buyProductPanel, "Buy a Product");

			actionPanels.put("Create a Wallet", createWalletPanel);
			actionPanels.put("Remove a Product", removeProductPanel);
			actionPanels.put("Display Products", displayProductsPanel);
			actionPanels.put("Send a Message", sendMessagePanel);
			actionPanels.put("Check Messages", showMessagesPanel);
			actionPanels.put("Buy a Product", buyProductPanel);
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
						"Send a Message", "Check Messages", "Buy a Product"
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
					fileChooser.addActionListener(this);

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
				if (actionName.equals("Back") || actionName.equals(JFileChooser.CANCEL_SELECTION)) {
					parentPanel.showRootMenu();
				}
				else {
					boolean result = gc.addWalletToUser(fileChooser.getSelectedFile().getPath()) == CreateWalletResult.SUCCESS;
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
		private class ShowMessagesPanel extends JPanel implements UserActionPanel, ActionListener {
			private final UserRootMenu parentPanel;

			private JList<Message> messageJList;

			public ShowMessagesPanel(UserRootMenu parentPanel) {
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

				ArrayList<Message> messages = gc.getUserMessages();
				DefaultListModel<Message> listModel = new DefaultListModel<>();
				listModel.addAll(messages);

				messageJList = new JList<>(listModel);
				messageJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				messageJList.setSelectedIndex(0);
				messageJList.setVisibleRowCount(8);
				JScrollPane listScrollPane = new JScrollPane(messageJList);

				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridy = 1;
				gridBagConstraints.anchor = GridBagConstraints.CENTER;
				this.add(listScrollPane, gridBagConstraints);

				gridBagConstraints.gridwidth = 1;
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 2;
				JButton btnReply = new JButton("Reply");
				btnReply.addActionListener(this);
				this.add(btnReply, gridBagConstraints);

				gridBagConstraints.gridx = 1;
				JButton btnDelete = new JButton("Delete");
				btnDelete.addActionListener(this);
				this.add(btnDelete, gridBagConstraints);
			}

			@Override
			public void showPanel() {
				ArrayList<Message> messages = gc.getUserMessages();
				DefaultListModel<Message> listModel = new DefaultListModel<>();
				listModel.addAll(messages);
				messageJList.setModel(listModel);
				messageJList.setSelectedIndex(0);
				messageJList.updateUI();
				this.updateUI();
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				String actionName = e.getActionCommand();
				if (actionName.equals("Back")) {
					parentPanel.showRootMenu();
				}
				else if (actionName.equals("Reply")) {
					String response = JOptionPane.showInputDialog(null, "What message would you like to send?", "Reply to message", JOptionPane.QUESTION_MESSAGE);
					if (response.length() != 0) {
						String replyResult = gc.replyToMessage(messageJList.getSelectedValue(), response);
						JOptionPane.showMessageDialog(null, replyResult);
						if (replyResult.equals("Message sent.")) {
							this.parentPanel.showRootMenu();
						}
					}
				}
				else {
					String deleteResult = gc.deleteMessage(messageJList.getSelectedValue());
					JOptionPane.showMessageDialog(null, deleteResult);
					if (deleteResult.equals("Message deleted.")) {
						this.parentPanel.showRootMenu();
					}

				}
			}
		}

		private class BuyProductPanel extends JPanel implements UserActionPanel, ActionListener {
			private final UserRootMenu parentPanel;

			private JFileChooser fileChooser;
			private JList<Product> productJList;

			private CardLayout buyProductLayout;

			public BuyProductPanel(UserRootMenu parentPanel) {
				this.parentPanel = parentPanel;
				JButton backButton = new JButton("Back");
				backButton.addActionListener(this);

				buyProductLayout = new CardLayout();
				this.setLayout(buyProductLayout);

				JPanel selectWalletDirectory = new JPanel();

				selectWalletDirectory.setLayout(new GridBagLayout());
				GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints.insets = new Insets(10, 10, 10, 10);

				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 0;
				selectWalletDirectory.add(backButton, gridBagConstraints);

				gridBagConstraints.gridy = 1;
				fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new java.io.File("."));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.addActionListener(this);
				selectWalletDirectory.add(fileChooser, gridBagConstraints);

				this.add(selectWalletDirectory, "select wallet");

				JPanel selectProductForPurchasePanel = new JPanel();
				selectProductForPurchasePanel.setLayout(new GridBagLayout());

				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
				selectProductForPurchasePanel.add(backButton, gridBagConstraints);

				ArrayList<Product> products = gc.getPurchasableProducts(true);
				products.sort(Product::compareToWithPrice);
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
				selectProductForPurchasePanel.add(listScrollPane, gridBagConstraints);

				JButton btnSendMessage = new JButton("Buy Product");
				btnSendMessage.addActionListener(this);
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 2;
				selectProductForPurchasePanel.add(btnSendMessage, gridBagConstraints);

				this.add(selectProductForPurchasePanel, "products panel");

				buyProductLayout.show(this,"select wallet");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				String actionName = e.getActionCommand();
				if (actionName.equals("Back") || actionName.equals(JFileChooser.CANCEL_SELECTION)) {
					this.parentPanel.showRootMenu();
				}
				else if (actionName.equals(JFileChooser.APPROVE_SELECTION)) {
					if (gc.walletInDBMatchesGivenPath(fileChooser.getSelectedFile().getPath())) {
						this.buyProductLayout.show(this,"products panel");
						this.updateProducts();
					}
					else {
						JOptionPane.showMessageDialog(null, "Invalid directory, try again.");
					}
				}
				else {
					String warning = "Are you sure you want to buy " + productJList.getSelectedValue() + "?";
					boolean confirmDialog = JOptionPane.showConfirmDialog(null, warning, "Confirm purchase", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
					if (confirmDialog) {
						String deleteResult = gc.buyProduct(fileChooser.getSelectedFile().getPath(), productJList.getSelectedValue());
						JOptionPane.showMessageDialog(null, deleteResult);
						if (deleteResult.equals("Item purchased.")) {
							this.parentPanel.showRootMenu();
						}
					}
				}
			}

			private void updateProducts() {
				ArrayList<Product> messages = gc.getPurchasableProducts(true);
				DefaultListModel<Product> listModel = new DefaultListModel<>();
				listModel.addAll(messages);
				productJList.setModel(listModel);
				productJList.setSelectedIndex(0);
				productJList.updateUI();
				this.updateUI();
			}

			@Override
			public void showPanel() {
				this.buyProductLayout.show(this,"select wallet");
				fileChooser.setCurrentDirectory(new java.io.File("."));
				updateProducts();
			}
		}
	}
}
