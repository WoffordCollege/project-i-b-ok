package edu.wofford.wocoin.console;

import io.bretty.console.view.ActionView;
import io.bretty.console.view.MenuView;
import io.bretty.console.view.ViewConfig;

import java.util.Scanner;

public abstract class CustomActionView extends ActionView {
    public CustomActionView(String runningTitle, String nameInParentMenu) {
        super(runningTitle, nameInParentMenu);
    }

    public CustomActionView(String runningTitle, String nameInParentMenu, ViewConfig viewConfig, Scanner keyboard) {
        super(runningTitle, nameInParentMenu, viewConfig);
        this.keyboard = keyboard;
    }

    public CustomActionView(String runningTitle, String nameInParentMenu, ViewConfig viewConfig) {
        super(runningTitle, nameInParentMenu, viewConfig);
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

    @Override
    public void display() {
        this.println();
        this.println(this.runningTitle);
        this.executeCustomAction();
        this.goBack();
    }
}
