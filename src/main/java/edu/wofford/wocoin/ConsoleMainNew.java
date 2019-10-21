package edu.wofford.wocoin;

import java.util.Scanner;

public class ConsoleMainNew {

    public static void main(String[] args) {
        int login = printLogin();
        if (login == 1) {
            System.exit();
        } else {
            Scanner inputPassword = new Scanner(System.in);
            String password = inputPassword.next();
            boolean state = administratorLogIn(password);
            if (state) {
                int adminInput = adminisratorLoggedIn();
            }
        }
    }


    private static int printLogin() {
        System.out.println("1: exit\n2: administrator");
        Scanner inputScanner = new Scanner(System.in);
        return inputScanner.nextInt();
    }

    private static int adminisratorLoggedIn() {
        System.out.println("1: back\n2: add user\n3. remove user");
        Scanner inputScanner = new Scanner(System.in);
        return inputScanner.nextInt();
    }

    private static boolean administratorLogIn (String password) {
        return password.equals("adminpwd");
    }
}
