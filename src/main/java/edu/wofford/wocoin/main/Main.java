package edu.wofford.wocoin.main;

import edu.wofford.wocoin.ConsoleController;
import edu.wofford.wocoin.gui.MainMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;


public class Main {
    public static void main(String[] args) {   
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(ch.qos.logback.classic.Level.OFF);
        if (args.length >= 1) {
            String[] realArgs = Arrays.copyOfRange(args, 1, args.length);
            if (args[0].equals("0")) {
                Feature00Main.main(realArgs);
            }
            else if (args[0].equals("1") || args[0].equals("2") || args[0].equals("3")
                  || args[0].equals("4") || args[0].equals("5") || args[0].equals("6")) {
                MainMenu.main(new String[] {args[1]});
            }
            else {
                System.out.println("Feature " + args[0] + " is not valid.");
            }

        } else {
            System.out.println("You must specify a feature number.");
        }
    }
}