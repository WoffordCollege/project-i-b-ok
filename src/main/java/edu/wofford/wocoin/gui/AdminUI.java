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
			JPanel adminOptionsView = new JPanel();
			adminOptionsView.setLayout(new GridLayout(0, 3));
			JButton btnAddUser = new JButton("Add a User");
			btnAddUser.addActionListener(this);
			adminOptionsView.add(btnAddUser);

			JButton btnRemoveUser = new JButton("Remove a User");
			btnRemoveUser.addActionListener(this);
			adminOptionsView.add(btnRemoveUser);

			JButton btnTransferFunds = new JButton("Transfer Funds");
			btnTransferFunds.addActionListener(this);
			adminOptionsView.add(btnTransferFunds);

			this.add(adminOptionsView, "admin options menu");

			AddUserPanel addUserPanel = new AddUserPanel(this);
			this.add(addUserPanel, "Add a User");

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
					parentPanel.showRootMenu();
				}
				else {
					String addUserResult = gc.addUser(txtNewUser.getText(), new String(txtNewPassword.getPassword()));
					JOptionPane.showMessageDialog(null, addUserResult);
					if (addUserResult.equals("User added.")) {
						this.parentPanel.showRootMenu();
					}
				}
			}
		}

		private class RemoveUserPanel extends JPanel implements ActionListener {
			private final AdminRootMenu parentPanel;

			public RemoveUserPanel(AdminRootMenu parentPanel) {
				this.parentPanel = parentPanel;
			}

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		}

		private class TransferFundsPanel extends JPanel implements ActionListener {
			private final AdminRootMenu parentPanel;

			public TransferFundsPanel(AdminRootMenu parentPanel) {
				this.parentPanel = parentPanel;
			}

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		}

		/*
		private class AddUserAction extends CustomActionView {
        public AddUserAction(ViewConfig viewConfig, Scanner keyboard) {
            super("Add User", "add user", viewConfig, keyboard);
        }

        @Override
        public void executeCustomAction() {
            String username = this.prompt("Enter a username to add: ", String.class);
            String password = this.prompt("Enter a password for the user: ", String.class);

            this.println(cc.addUser(username, password));
        }
    }

    private class RemoveUserAction extends CustomActionView {
        public RemoveUserAction(ViewConfig viewConfig, Scanner keyboard) {
            super("Remove User", "remove user", viewConfig, keyboard);
        }

        @Override
        public void executeCustomAction() {
            String username = this.prompt("Please enter the username of the account to be removed: ", String.class);
            this.println(cc.removeUser(username));
        }
    }

    private class TransferFundsAction extends CustomActionView {
        public TransferFundsAction(ViewConfig viewConfig, Scanner keyboard) {
            super("Transfer WoCoins to User", "transfer WoCoins", viewConfig, keyboard);
        }

        @Override
        public void executeCustomAction() {
            String username = this.prompt("Enter the username of the user to transfer WoCoins to: ", String.class);
            if (!cc.getSqlController().lookupUser(username)) {
                this.println("No such user.");
            }
            else if (!cc.getSqlController().findWallet(username)){
                this.println("User has no wallet.");
            }
            else {
                int coinsToTransfer;

                do {
                    coinsToTransfer = this.prompt("Enter the amount of WoCoins to transfer to the user: ", Integer.class);
                    if (coinsToTransfer <= 0) {
                        this.println("Invalid value.");
                        this.println("Expected an integer value greater than or equal to 1.");
                    }
                } while (coinsToTransfer <= 0);

                this.println(cc.transferWocoinsToUser(username, coinsToTransfer));
            }
        }
		 */
	}
}
