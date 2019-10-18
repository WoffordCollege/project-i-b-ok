package edu.wofford.wocoin;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class ConsoleMainTest {
    @Test
    public void initialLoginScreen() {
        String programInput = "";
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        String actualOutput;
        try {
            System.setIn(new ByteArrayInputStream(programInput.getBytes()));
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            String[] args = {"wocoinDatabase.sqlite3"};
            ConsoleMain.main(args);
            actualOutput = outContent.toString();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        assertEquals("1: exit\n2: administrator\n", actualOutput);
    }

    @Test
    public void initialAdministratorLoggedInScreen() {
        String programInput = "2\nadminpwd";
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        String actualOutput;
        try {
            System.setIn(new ByteArrayInputStream(programInput.getBytes()));
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            String[] args = {"wocoinDatabase.sqlite3"};
            ConsoleMain.main(args);
            actualOutput = outContent.toString();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        assertEquals("1: exit\n2: adduser\n", actualOutput);
    }


    @Test
    public void testAddUser() {
        String programInput = "2\nadminpwd\n2\ntestuser testpass";
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        String actualOutput;
        try {
            System.setIn(new ByteArrayInputStream(programInput.getBytes()));
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            String[] args = {"wocoinDatabase.sqlite3"};
            ConsoleMain.main(args);
            actualOutput = outContent.toString();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        assertEquals("1: exit\n2: adduser\n", actualOutput);
    }
}
