package edu.wofford.wocoin.gui;

import io.bretty.console.view.MenuView;
import io.bretty.console.view.Validator;
import io.bretty.console.view.ViewConfig;

public class CustomMenuView extends MenuView {
    public CustomMenuView(String runningTitle, String nameInParentMenu) {
        super(runningTitle, nameInParentMenu);
    }

    public CustomMenuView(String runningTitle, String nameInParentMenu, ViewConfig viewConfig) {
        super(runningTitle, nameInParentMenu, viewConfig);
    }

    private boolean isValidIndex(int index) {
        return index >= 1 && index <= this.menuItems.size() + 1;
    }

    @Override
    public void display() {
        this.println();

        // print running title (e.g. "Create Item")
        this.println(this.runningTitle);

        // print all menu items
        // e.g.
        // 1) Create Item
        // 2) View Item
        // 3) ...

        String backOrQuit = this.parentView == null ? this.viewConfig.getQuitMenuName() : this.viewConfig.getBackMenuName();

        this.println(this.viewConfig.getIndexNumberFormatter().format(0) + backOrQuit);

        for (int i = 0; i < this.menuItems.size(); ++i) {
            this.println(this.viewConfig.getIndexNumberFormatter().format(i + 1) + this.menuItems.get(i).getNameInParentMenu());
        }


        // 4) Back/quit; always the last index


        // get a valid index number
        Validator<Integer> indexNumberValidator = new Validator<Integer>() {
            @Override
            public boolean isValid(Integer index) {
                return isValidIndex(index);
            }
        };

        int selection = this.prompt(this.viewConfig.getMenuSelectionMessage(), Integer.class, indexNumberValidator);

        // go parentView
        if (selection == 1) {
            this.goBack();
        } else {
            this.menuItems.get(selection - 2).display();
        }
    }

    @Override
    protected void goBack() {
        if (this.parentView != null) {
            this.onBack();

            // if the parent view is a menu, display that menu
            if(this.parentView instanceof MenuView){
                this.parentView.display();
            }
        }
        else {
            this.onQuit();
        }
    }
}
