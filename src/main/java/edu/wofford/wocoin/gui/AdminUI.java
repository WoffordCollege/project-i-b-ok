package edu.wofford.wocoin.gui;

import edu.wofford.wocoin.ConsoleController;
import edu.wofford.wocoin.SQLController;
import io.bretty.console.view.AbstractView;
import io.bretty.console.view.ViewConfig;

import java.math.BigInteger;
import java.util.Scanner;

/**
 * This class represents
 */
public class AdminUI extends CustomActionView {
    private ConsoleController cc;

    public AdminUI(ConsoleController cc) {
        super("", "administrator");
        this.cc = cc;
    }

    public AdminUI(ConsoleController cc, ViewConfig viewConfig, Scanner keyboard) {
        super("", "administrator", viewConfig, keyboard);
        this.cc = cc;
    }


    @Override
    public void executeCustomAction() {
        String password = this.prompt("Please provide the administrator password: ", String.class);

        if (cc.adminLogin(password)) {
            new AdminRootMenu(this.parentView, cc, this.viewConfig, this.keyboard).display();
        }
        else {
            this.println("Incorrect administrator password.");
            this.goBack();
        }
    }


    private class AdminRootMenu extends CustomMenuView {
        private ConsoleController cc;

        public AdminRootMenu(AbstractView parentView, ConsoleController cc, ViewConfig viewConfig, Scanner keyboard) {
            super("Welcome back, Administrator", "", viewConfig);

            this.parentView = parentView;
            this.keyboard = keyboard;

            AddUserAction addUserAction = new AddUserAction(viewConfig, keyboard);
            RemoveUserAction removeUserAction = new RemoveUserAction(viewConfig, keyboard);
            TransferFundsAction transferFundsAction = new TransferFundsAction(viewConfig, keyboard);


            this.addMenuItem(addUserAction);
            this.addMenuItem(removeUserAction);
            this.addMenuItem(transferFundsAction);
        }
    }

    @Override
    protected void onBack() {
        cc.doLogout();
        super.onBack();
    }

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
                int coinsToTransfer = this.prompt("Enter the amount of WoCoins to transfer to the user: ", Integer.class);
                if (coinsToTransfer > 0) {
					this.println(cc.transferWocoinsToUser(username, coinsToTransfer));
                }
                else {
                    this.println("Invalid value.");
                    this.println("Expected an integer value greater than or equal to 1.");
                }
            }
        }
    }
}
