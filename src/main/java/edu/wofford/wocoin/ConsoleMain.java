package edu.wofford.wocoin;

import java.util.Scanner;

public class ConsoleMain implements UIController {

    private AccessController ac;

    private AccessController.Result acResult;
    private AccessController.AccessOptions[] acOptions;

    public ConsoleMain() {
        ac = new AccessController(this);
        acResult = null;
        acOptions = null;
    }

    public ConsoleMain(String dbName) {
        ac = new AccessController(this, dbName);
        acResult = null;
        acOptions = null;
    }

    private void takeInput() {
        if (acOptions.length == 0){
            this.printLogin();
            this.takeOptionInput();
        }
    }

    private void printLogin() {
        System.out.println("1: exit\n2: administrator");
    }

    private int takeOptionInput() {
        Scanner inputScanner = new Scanner(System.in);

        return inputScanner.nextInt();
    }

    @Override
    public void updateDisplay(AccessController.Result actionResult, AccessController.AccessOptions[] userOptions) {
        acResult = actionResult;
        acOptions = userOptions;
        this.takeInput();
    }

    public static void main(String[] args){
        ConsoleMain consoleMain = null;
        if (args.length == 1){
            consoleMain = new ConsoleMain(args[0]);
        }
        else {
            consoleMain = new ConsoleMain();
        }
        consoleMain.takeInput();
    }
}
