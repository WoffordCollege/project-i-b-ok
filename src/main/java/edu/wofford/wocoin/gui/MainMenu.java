package edu.wofford.wocoin.gui;

import edu.wofford.wocoin.ConsoleController;
import edu.wofford.wocoin.SQLController;
import io.bretty.console.view.ViewConfig;

import java.util.Scanner;

public class MainMenu extends CustomMenuView{
    private ConsoleController cc;
    public static Scanner keyboard;

    public MainMenu(ConsoleController consoleController) {
        super("Welcome to Wocoin!", "");
        this.cc = consoleController;

        this.viewConfig = new ViewConfig.Builder()
                .setIndexNumberFormatter(index -> (index + 1) + ": ")
                .setBackMenuName("back")
                .setQuitMenuName("quit")
                .build();

        keyboard = new Scanner(System.in);

        AdminUI adminUI = new AdminUI(cc, this.viewConfig);
        UserUI userUI = new UserUI(cc, this.viewConfig);

        adminUI.setParentView(this);
        userUI.setParentView(this);

        this.addMenuItem(adminUI);
        this.addMenuItem(userUI);
    }

    public static void main(String[] args) {
        MainMenu cc = null;
        if (args.length == 1) {
            cc = new MainMenu(new ConsoleController(new SQLController(args[0])));
        }
        else {
            cc = new MainMenu(new ConsoleController(new SQLController()));
        }

        cc.display();
    }
}
