package edu.wofford.wocoin.gui;

import edu.wofford.wocoin.ConsoleController;
import io.bretty.console.view.AbstractView;
import io.bretty.console.view.Validator;
import io.bretty.console.view.ViewConfig;

import java.util.Scanner;

public class AdminUI extends CustomActionView {
    private ConsoleController cc;

    public AdminUI(ConsoleController cc) {
        super("Enter the administrator password: ", "administrator");
        this.cc = cc;
    }

    public AdminUI(ConsoleController cc, ViewConfig viewConfig, Scanner keyboard) {
        super("Enter the administrator password: ", "administrator", viewConfig, keyboard);
        this.cc = cc;
    }


    @Override
    public void executeCustomAction() {
        String password = this.prompt("", String.class);

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
            super("Welcome, Administrator", "", viewConfig);

            this.parentView = parentView;
            this.keyboard = keyboard;

            CustomActionView addUserAction = new CustomActionView("Add User", "add user", viewConfig, keyboard) {
                @Override
                public void executeCustomAction() {
                    String username = this.prompt("Enter your username: ", String.class);
                    String password = this.prompt("Enter your password: ", String.class);

                    this.println(cc.addUser(username, password));
                    this.println("Invalid input");
                }
            };

            CustomActionView removeUserAction = new CustomActionView("Remove User", "remove user", viewConfig, keyboard) {
                @Override
                public void executeCustomAction() {
                    String username = this.prompt("Please enter the username of the account to be removed: ", String.class);
                    this.println(cc.removeUser(username));
                }
            };

            this.addMenuItem(addUserAction);
            this.addMenuItem(removeUserAction);
        }
    }

    @Override
    protected void onBack() {
        cc.doLogout();
        super.onBack();
    }
}
