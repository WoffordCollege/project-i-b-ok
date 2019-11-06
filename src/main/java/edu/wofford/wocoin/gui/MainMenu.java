package edu.wofford.wocoin.gui;

import edu.wofford.wocoin.ConsoleController;
import edu.wofford.wocoin.SQLController;
import io.bretty.console.view.ViewConfig;

import java.util.Scanner;

public class MainMenu extends CustomMenuView {
    private ConsoleController cc;

    public MainMenu(ConsoleController consoleController, Scanner keyboard) {
        super("Welcome to Wocoin!", "");
        this.cc = consoleController;

        this.viewConfig = new ViewConfig.Builder()
                                        .setIndexNumberFormatter(index -> (index + 1) + ": ")
                                        .setBackMenuName("back")
                                        .setQuitMenuName("exit")
                                        .build();

        this.keyboard = keyboard;

        AdminUI adminUI = new AdminUI(cc, this.viewConfig, this.keyboard);
        UserUI userUI = new UserUI(cc, this.viewConfig, this.keyboard);

        this.addMenuItem(adminUI);
        this.addMenuItem(userUI);
    }

    @Override
    protected void onQuit() {
    }

    public static void main(String[] args) {
        MainMenu cc = null;
        if (args.length == 1) {
            cc = new MainMenu(new ConsoleController(new SQLController(args[0])), new Scanner(System.in));
        }
        else {
            cc = new MainMenu(new ConsoleController(new SQLController()), new Scanner(System.in));
        }

        cc.display();
    }
}
