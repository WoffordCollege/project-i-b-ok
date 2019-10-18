package edu.wofford.wocoin;

import java.util.Scanner;

public class ConsoleMain implements UIController {

    private AccessController ac;

    private AccessController.Result acResult;
    private AccessController.AccessOptions[] acOptions;

    private enum UIState {START, LOGIN, ADMINISTRATOR, STANDARD}

    private UIState currentState;

    public ConsoleMain() {
        this("wocoinDatabase.sqlite3");
    }

    public ConsoleMain(String dbName) {
        ac = new AccessController(this, dbName);
        acResult = null;
        acOptions = new AccessController.AccessOptions[0];
        currentState = UIState.START;
    }

    private void takeInput() {
        switch (currentState){
            case START:
                this.printLogin();
                break;
            case LOGIN:
                break;
            case ADMINISTRATOR:
                this.printAdministratorLoggedIn();
                break;
            case STANDARD:
                break;
        }

        int chosenOption = this.takeOptionInput();

        switch (currentState) {
            case START:
                if (chosenOption == 1) {
                    System.exit(0);
                }
                else if (chosenOption == 2) {
                    String[] userAndPass = this.takeArgumentInput(1);
                    ac.login("", userAndPass[0]);
                }
                break;
            case LOGIN:
                break;
            case ADMINISTRATOR:
                if (chosenOption == 1) {
                    currentState = UIState.START;
                    this.takeInput();
                }
                else if (chosenOption == 2) {
                    String[] newUser = this.takeArgumentInput(2);
                    ac.addUser(newUser[0], newUser[1]);
                }
                break;
            case STANDARD:
                break;
        }
    }

    private String[] takeArgumentInput(int argLength) {
        Scanner inputScanner = new Scanner(System.in);
        String currentLine = inputScanner.nextLine();
        String[] returnVal = currentLine.split(" ");

        while (returnVal.length != argLength) {
            currentLine = inputScanner.nextLine();
            returnVal = currentLine.split(" ");
        }

        return returnVal;
    }

    private void printLogin() {
        System.out.println("1: exit\n2: administrator");
    }

    private void printAdministratorLoggedIn() {
        System.out.println("1: back\n2: add user");
    }

    private int takeOptionInput() {
        Scanner inputScanner = new Scanner(System.in);
        return inputScanner.nextInt();
    }

    @Override
    public void updateDisplay(AccessController.Result actionResult, AccessController.AccessOptions[] userOptions) {
        this.updateDisplay(actionResult, userOptions, new String[0]);
    }

    @Override
    public void updateDisplay(AccessController.Result actionResult, AccessController.AccessOptions[] userOptions, String[] args) {
        acResult = actionResult;
        acOptions = userOptions;

        switch (currentState) {
            case START:
                if (acResult == AccessController.Result.SUCCESS){
                    currentState = UIState.ADMINISTRATOR;
                }
                else {
                    System.out.println("Incorrect Administrator Password.");
                }
                break;
            case LOGIN:
                break;
            case ADMINISTRATOR:
                if (acResult == AccessController.Result.SUCCESS){
                    if (args.length >= 1) {
                        System.out.println(args[0] + " was added.");
                    }
                }
                else if (acResult == AccessController.Result.INVALID_USERNAME) {
                    if (args.length >= 1) {
                        System.out.println(args[0] + " already exists.");
                    }
                }
                else {
                    System.out.println("Unhandled exception.");
                }
                break;
            case STANDARD:
                break;
        }

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
