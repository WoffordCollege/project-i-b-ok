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

    /**
    * displays initial screen 
    * If administrator, then displays for add or remove users
    */
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
                else if (chosenOption == 3) {
                    String[] removedUser = this.takeArgumentInput(1);
                    ac.removeUser(removedUser[0]);
                }
                break;
            case STANDARD:
                break;
        }
    }
    
    /**
    * takes in arguments from users
    */
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
    
    /**
    * Display initial screen
    */
    private void printLogin() {
        System.out.println("1: exit\n2: administrator");
    }

    /**
    * display administrator screen
    */
    private void printAdministratorLoggedIn() {
        System.out.println("1: back\n2: add user\n3. remove user");
    }
    
    /**
    * scans user inputScanner
    */
    private int takeOptionInput() {
        Scanner inputScanner = new Scanner(System.in);
        return inputScanner.nextInt();
    }

    /**
    * displays accesscontrollers ResultS
    */
    @Override
    public void updateDisplay(AccessController.Result actionResult, AccessController.AccessOptions[] userOptions) {
        this.updateDisplay(actionResult, userOptions, new String[0]);
    }

    /**
    * displays accesscontrollers ResultS
    */
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
                    if (args.length >= 2) {
                        if (args[0].equals("add")){
                            System.out.println(args[1] + " was added.");
                        }
                        else if (args[0].equals("remove")){
                            System.out.println(args[1] + " was removed.");
                        }
                    }
                }
                else if (acResult == AccessController.Result.INVALID_USERNAME) {
                    if (args.length >= 2) {
                        if (args[0].equals("add")){
                            System.out.println(args[1] + " already exists.");
                        }
                        else if (args[0].equals("remove")){
                            System.out.println(args[1] + " does not exist.");
                        }
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

