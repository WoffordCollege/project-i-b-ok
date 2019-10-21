package edu.wofford.wocoin;

import java.util.Scanner;

public class ConsoleMain {

    private Scanner scanner;

    private SQLController sqlController;

    public enum UIState {EXIT, LOGIN, ADMINISTRATOR}

    private UIState currentState;

    public ConsoleMain() {
        this(new Scanner(System.in));
    }

    public ConsoleMain(Scanner scanner) {
        this.scanner = scanner;
        this.currentState = UIState.LOGIN;
        this.sqlController = new SQLController();
    }

    public ConsoleMain(String dbname) {
        this(new Scanner(System.in), dbname);
    }

    public ConsoleMain(Scanner scanner, String dbname) {
        this.scanner = scanner;
        this.currentState = UIState.LOGIN;
        this.sqlController = new SQLController(dbname);
    }


    private void showUI() {
        switch (currentState) {
            case LOGIN:
                doLoginUI();
                break;
            case ADMINISTRATOR:
                doAdministratorUI();
                break;
        }
    }


    private void doLoginUI() {
        System.out.println("1: exit\n2: administrator");

        int option = scanner.nextInt();
        if (option == 1) {
            currentState = UIState.EXIT;
        }
        if (option == 2){
            String password = scanner.next();
            if (password.equals("adminpwd")){
                currentState = UIState.ADMINISTRATOR;
            }
            else {
                System.out.println("Incorrect administrator password.");
            }
        }

        showUI();
    }

    private void doAdministratorUI() {
        System.out.println("1: back\n2: add user\n3. remove user");
        while (currentState == UIState.ADMINISTRATOR) {
            int option = scanner.nextInt();
            if (option == 1) {
                currentState = UIState.LOGIN;
            } else if (option == 2) {
                String username = scanner.next();
                String password = scanner.next();
                SQLController.AddUserResult result = sqlController.insertUser(username, password);

                switch (result) {
                    case ADDED:
                        System.out.println(username + " was added.");
                        break;
                    case DUPLICATE:
                        System.out.println(username + " already exists.");
                        break;
                    case NOTADDED:
                        System.out.println(username + " was not added.");
                        break;
                }
            } else if (option == 3) {
                String username = scanner.next();
                SQLController.RemoveUserResult result = sqlController.removeUser(username);

                switch (result) {
                    case REMOVED:
                        System.out.println(username + " was removed.");
                        break;
                    case NORECORD:
                        System.out.println(username + " does not exist.");
                        break;
                    case NOTREMOVED:
                        System.out.println(username + " was not removed.");
                        break;
                }
            }
        }

        showUI();
    }

    private int administratorLoggedIn() {
        return scanner.nextInt();
    }

    private static boolean administratorLogIn (String password) {
        return password.equals("adminpwd");
    }


    public static void main(String[] args) {
        ConsoleMain cm = null;
        if (args.length == 1) {
            cm = new ConsoleMain(args[0]);
        }
        else {
            cm = new ConsoleMain();
        }
        cm.showUI();
    }
}
