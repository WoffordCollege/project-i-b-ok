package edu.wofford.wocoin.gui;

import edu.wofford.wocoin.ConsoleController;
import edu.wofford.wocoin.WalletUtilities;
import io.bretty.console.view.AbstractView;
import io.bretty.console.view.Validator;
import io.bretty.console.view.ViewConfig;

import java.util.Scanner;

public class UserUI extends CustomActionView {
    private ConsoleController cc;

    public UserUI(ConsoleController cc, ViewConfig viewConfig) {
        super("Please enter your username and password separated by a space.", "user", viewConfig);
        this.cc = cc;
    }

    public UserUI(ConsoleController cc, ViewConfig viewConfig, Scanner keyboard) {
        super("Please enter your username and password separated by a space.", "user", viewConfig, keyboard);
        this.cc = cc;
    }

    @Override
    public void executeCustomAction() {
        String username = this.prompt("Enter your username: ", String.class);
        String password = this.prompt("Enter your password: ", String.class);

        if (!cc.userLogin(username, password)){
            this.println("No such user.");
            this.goBack();
        }
        else {
            new UserRootMenu(this.parentView, username, viewConfig, this.keyboard).display();
        }
    }

    private class UserRootMenu extends CustomMenuView {

        public UserRootMenu(AbstractView parentView, String user, ViewConfig viewConfig, Scanner keyboard) {
            super("Welcome, " + user, "", viewConfig, keyboard);

            this.parentView = parentView;

            CustomActionView createWalletAction = new CustomActionView("Create a Wallet", "create wallet", viewConfig, keyboard) {
                @Override
                public void executeCustomAction() {
                    boolean userStillCreatingWallet = true;

                    if (cc.userHasWallet()) {
                        this.println("Your account has an associated wallet.");
                        userStillCreatingWallet = this.confirmDialog("Would you like to delete this wallet and create a new wallet?");

                        if (userStillCreatingWallet) {
                            this.println("Wallet deleted.");
                        }
                    }

                    if (userStillCreatingWallet) {
                        String path = this.prompt("Enter the file path for the wallet: ", String.class);

                        WalletUtilities.CreateWalletResult result = cc.addWalletToUser(path);

                        this.println(result == WalletUtilities.CreateWalletResult.SUCCESS ? "Wallet added." : "Action Canceled");

                    }
                }
            };

            this.addMenuItem(createWalletAction);
        }


        @Override
        protected void onBack() {
            cc.doLogout();
            super.onBack();
        }
    }
}
