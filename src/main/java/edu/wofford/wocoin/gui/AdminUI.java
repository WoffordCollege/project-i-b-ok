package edu.wofford.wocoin.gui;

import edu.wofford.wocoin.GUIController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class AdminUI extends JPanel implements ActionListener {
	private GUIController gc;
	private CardLayout loginScreenLayout;

	private JPasswordField txtPassword;

	private AdminRootMenu adminRootMenu;

	public AdminUI(GUIController gc) {
		this.gc = gc;
		loginScreenLayout = new CardLayout();

		setLayout(loginScreenLayout);

		JPanel pnlLogin = new JPanel();
		setupLoginPanel(pnlLogin);
		this.add(pnlLogin, "login screen");

		adminRootMenu = new AdminRootMenu();
		this.add(adminRootMenu, "user control screen");


	}

	public void setupLoginPanel(JPanel pnlLogin) {
		pnlLogin.setLayout(new FlowLayout());

		txtPassword = new JPasswordField(20);
		pnlLogin.add(new JLabel("Password: "));
		pnlLogin.add(txtPassword);

		JButton loginButton = new JButton("Login");
		loginButton.setActionCommand("login");
		loginButton.addActionListener(this);
		pnlLogin.add(loginButton);
	}

	public void logout() {
		gc.doLogout();
		this.txtPassword.setText("");
		this.loginScreenLayout.show(this, "login screen");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Logout")) {
			this.loginScreenLayout.show(this, "login screen");
			adminRootMenu.showRootMenu();
		}
		else {
			txtPassword.setBackground(Color.WHITE);
			if (gc.adminLogin(new String(txtPassword.getPassword()))) {
				this.loginScreenLayout.show(this, "user control screen");
				adminRootMenu.showRootMenu();
				txtPassword.setText("");
			} else {
				txtPassword.setBackground(Color.RED);
			}
		}
	}

	private class AdminRootMenu extends JPanel implements ActionListener {
		private JPanel userOptionsMenu;
		private CardLayout rootMenuLayout;
		private HashMap<String, UserActionPanel> actionPanels;

		public AdminRootMenu() {
			userOptionsMenu = new JPanel();
			userOptionsMenu.setLayout(new GridLayout(0, 3));
			JButton btnAddUser = new JButton("Add a User");
			btnAddUser.addActionListener(this);
			userOptionsMenu.add(btnAddUser);

			JButton btnRemoveUser = new JButton("Remove a User");
			btnRemoveUser.addActionListener(this);
			userOptionsMenu.add(btnRemoveUser);

			JButton btnTransferFunds = new JButton("Transfer Funds");
			btnTransferFunds.addActionListener(this);
			userOptionsMenu.add(btnTransferFunds);

			rootMenuLayout = new CardLayout();
			this.setLayout(rootMenuLayout);
			this.add(userOptionsMenu, "admin options menu");

			AddUserPanel addUserPanel = new AddUserPanel(this);
			this.add(addUserPanel, "Add a User");

			RemoveUserPanel removeUserPanel = new RemoveUserPanel(this);
			this.add(removeUserPanel, "Remove a User");

			TransferFundsPanel transferFundsPanel = new TransferFundsPanel(this);
			this.add(transferFundsPanel, "Transfer Funds");

		}

		public void showRootMenu() {
			rootMenuLayout.show(this, "admin options menu");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			this.rootMenuLayout.show(this, e.getActionCommand());
		}

		private class AddUserPanel extends JPanel implements ActionListener {
			private final AdminRootMenu parentPanel;
			private JTextField txtNewUser;
			private JPasswordField txtNewPassword;

			public AddUserPanel(AdminRootMenu parentPanel) {
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

				txtNewUser = new JTextField(20);
				gridBagConstraints.gridy = 1;
				gridBagConstraints.anchor = GridBagConstraints.CENTER;
				this.add(txtNewUser, gridBagConstraints);

				txtNewPassword = new JPasswordField(20);
				gridBagConstraints.gridx = 1;
				this.add(txtNewPassword, gridBagConstraints);

				JButton btnAddNewUser = new JButton("Add New User");
				btnAddNewUser.addActionListener(this);
				gridBagConstraints.gridx = 2;
				this.add(btnAddNewUser, gridBagConstraints);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				String actionName = e.getActionCommand();
				if (actionName.equals("Back")) {
					txtNewUser.setText("");
					txtNewPassword.setText("");
					parentPanel.showRootMenu();
				}
				else {
					String addUserResult = gc.addUser(txtNewUser.getText(), new String(txtNewPassword.getPassword()));
					JOptionPane.showMessageDialog(null, addUserResult);
					if (addUserResult.equals(txtNewUser.getText() + " was added.")) {
						txtNewUser.setText("");
						txtNewPassword.setText("");
						this.parentPanel.showRootMenu();
					}
				}
			}
		}

		private class RemoveUserPanel extends JPanel implements ActionListener {
			private final AdminRootMenu parentPanel;

			private JTextField txtRemoveUser;


			public RemoveUserPanel(AdminRootMenu parentPanel) {
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

				txtRemoveUser = new JTextField(20);
				gridBagConstraints.gridy = 1;
				gridBagConstraints.anchor = GridBagConstraints.CENTER;
				this.add(txtRemoveUser, gridBagConstraints);

				JButton btnAddNewUser = new JButton("Remove User");
				btnAddNewUser.addActionListener(this);
				gridBagConstraints.gridx = 1;
				this.add(btnAddNewUser, gridBagConstraints);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				String actionName = e.getActionCommand();
				if (actionName.equals("Back")) {
					txtRemoveUser.setText("");
					parentPanel.showRootMenu();
				}
				else {
					String removeUserResult = gc.removeUser(txtRemoveUser.getText());
					JOptionPane.showMessageDialog(null, removeUserResult);
					if (removeUserResult.equals(txtRemoveUser.getText() + " was removed.")) {
						txtRemoveUser.setText("");
						this.parentPanel.showRootMenu();
					}
				}
			}
		}

		private class TransferFundsPanel extends JPanel implements ActionListener {
			private final AdminRootMenu parentPanel;

			private JTextField txtUser;
			private JSpinner txtWocoinAmount;

			public TransferFundsPanel(AdminRootMenu parentPanel) {
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

				txtUser = new JTextField(20);
				gridBagConstraints.gridy = 1;
				gridBagConstraints.anchor = GridBagConstraints.CENTER;
				this.add(txtUser, gridBagConstraints);

				txtWocoinAmount = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
				gridBagConstraints.gridx = 1;
				this.add(txtWocoinAmount, gridBagConstraints);

				JButton btnAddNewUser = new JButton("Transfer Funds to User");
				btnAddNewUser.addActionListener(this);
				gridBagConstraints.gridx = 2;
				this.add(btnAddNewUser, gridBagConstraints);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				String actionName = e.getActionCommand();
				if (actionName.equals("Back")) {
					txtUser.setText("");
					txtWocoinAmount.setValue(1);
					parentPanel.showRootMenu();
				}
				else {
					String transferCoinResult = gc.transferWocoinsToUser(txtUser.getText(), (Integer) txtWocoinAmount.getValue());
					JOptionPane.showMessageDialog(null, transferCoinResult);
					if (transferCoinResult.equals("Transfer complete.")) {
						txtUser.setText("");
						txtWocoinAmount.setValue(1);
						parentPanel.showRootMenu();
					}
				}
			}
		}
	}
}
