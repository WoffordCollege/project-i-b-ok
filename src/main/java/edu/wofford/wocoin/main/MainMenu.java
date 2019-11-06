package edu.wofford.wocoin.main;

import edu.wofford.wocoin.ConsoleController;
import edu.wofford.wocoin.SQLController;
import io.bretty.console.view.MenuView;
import io.bretty.console.view.ViewConfig;

public class MainMenu extends MenuView{
    private ConsoleController cc;

    public MainMenu(ConsoleController consoleController) {
        super("Welcome to Wocoin!", "");
        this.cc = consoleController;

        this.viewConfig = new ViewConfig.Builder()
                .setIndexNumberFormatter(index -> (index + 1) + ": ")
                .setBackMenuName("back")
                .setQuitMenuName("quit")
                .build();

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
