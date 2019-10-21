package edu.wofford.wocoin;

import java.util.Scanner;

public class ConsoleMain {

    private Scanner scanner;

    public ConsoleMain() {
        this(new Scanner(System.in));
    }

    public ConsoleMain(Scanner scanner) {
        this.scanner = scanner;
    }


    private void beginUI() {
        int login = printLogin();
        if (login == 1) {
        } else {
            String password = scanner.next();
            boolean state = administratorLogIn(password);
            if (state) {
                int adminInput = administratorLoggedIn();
            }
        }
    }


    public static void main(String[] args) {
        ConsoleMain cm = new ConsoleMain();
        cm.beginUI();
    }


    private int printLogin() {
        System.out.println("1: exit\n2: administrator");
        return scanner.nextInt();
    }

    private int administratorLoggedIn() {
        System.out.println("1: back\n2: add user\n3. remove user");
        return scanner.nextInt();
    }

    private static boolean administratorLogIn (String password) {
        return password.equals("adminpwd");
    }
}
