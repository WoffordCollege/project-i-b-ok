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
        super("Enter the administrator password: ", "administrator", viewConfig);
        this.cc = cc;
        this.keyboard = keyboard;
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

            CustomActionView addUserAction = new CustomActionView("Add User", "add user", viewConfig) {
                @Override
                public void executeCustomAction() {
                    Validator<String> customValidator = s -> s.split(" ").length == 2;
                    String usernameAndPassword = this.prompt("Enter a username and password separated by a space for the user to add.", String.class, customValidator);
                    String username = usernameAndPassword.split(" ")[0];
                    String password = usernameAndPassword.split(" ")[1];

                    this.println(cc.addUser(username, password));
                    this.println("Invalid input");
                }
            };

            CustomActionView removeUserAction = new CustomActionView("Remove User", "remove user", viewConfig) {
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
