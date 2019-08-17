package edu.wofford.wocoin.main;

import edu.wofford.wocoin.Utilities;


public class Feature00Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            // The test database is populated with two users -- "jsmith" and
            // "jdoe" -- whose passwords are the same as their usernames.
            // It also uses the public keys from the sample wallets associated
            // with each username. Finally, it is preloaded with a set of
            // products being sold by those users.
            Utilities.createTestDatabase(args[0]);
        } else {
            System.out.println("You must specify the database filepath as a command-line argument.");
        }
    }
}
