package edu.wofford.wocoin.main;

import edu.wofford.wocoin.ConsoleController;
import edu.wofford.wocoin.SQLController;

import java.util.Scanner;

public class ConsoleMain {
    public static void main(String[] args) {
        ConsoleController cm = null;
        if (args.length == 1) {
            cm = new ConsoleController(new SQLController(args[0]));
        }
        else {
            cm = new ConsoleController(new SQLController());
        }

        Scanner scanner = new Scanner(System.in);
        int option = 0;

        while (cm.getCurrentState() != ConsoleController.UIState.EXIT) {
            System.out.println(cm.getCurrentUIString());
            switch (cm.getCurrentState()) {
                case EXIT:
                    break;
                case LOGIN:
                    option = scanner.nextInt();
                    if (option == 1) {
                        cm.exit();
                    }
                    if (option == 2){
                        String password = scanner.next();
                        if (!cm.adminLogin(password))
                            System.out.println("Incorrect administrator password.");
                    }
                    break;
                case ADMINISTRATOR:
                    option = scanner.nextInt();
                    if (option == 1) {
                        cm.doLogout();
                    } else if (option == 2) {
                        String username = scanner.next();
                        String password = scanner.next();
                        System.out.println(cm.addUser(username, password));
                    } else if (option == 3) {
                        String username = scanner.next();
                        System.out.println(cm.removeUser(username));
                    }
                    break;
            }
        }
    }
}
