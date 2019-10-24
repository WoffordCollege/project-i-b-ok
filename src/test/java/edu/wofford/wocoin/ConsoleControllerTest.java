package edu.wofford.wocoin;

import edu.wofford.wocoin.main.ConsoleMain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;

public class ConsoleControllerTest {

    @Before
    public void setUp(){
        File file = new File("test.db");
        if (file.exists()) {
            boolean delete = file.delete();
        }

        Utilities.createNewDatabase("test.db");
    }

    @After
    public void tearDown() {
        File file = new File("test.db");
        if (file.exists()) {
            boolean delete = file.delete();
        }
    }

    private String sendProgramInput(String input) {
        String actualOutput = null;
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            ConsoleMain.main(new String[]{"test.db"});
            actualOutput = outContent.toString();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        return actualOutput;
    }

    @Test
    public void testLoginScreenConsole() {
        String output = sendProgramInput("1");
        assertEquals("1: exit\n2: administrator\n", output);
    }


    @Test
    public void testAdministratorScreenConsole() {
        String output = sendProgramInput("2\nadminpwd\n1\n1");
        String expectedOutput = "1: back\n2: add user\n3: remove user";
        assertThat(output, containsString(expectedOutput));
    }

    @Test
    public void testAddUserConsole() {
        String output = sendProgramInput("2\nadminpwd\n2\nmarshall marshall\n1\n1");
        String expectedOutput = "marshall was added.";
        assertThat(output, containsString(expectedOutput));
        output = sendProgramInput("2\nadminpwd\n2\nmarshall marshall\n2\nmarshall marshall\n1\n1");
        expectedOutput = "marshall already exists.";
        assertThat(output, containsString(expectedOutput));
    }

    @Test
    public void testRemoveUserConsole() {
        String output = sendProgramInput("2\nadminpwd\n2\nmarshall marshall\n3\nmarshall\n1\n1");
        String expectedOutput = "marshall was removed.";
        assertThat(output, containsString(expectedOutput));
    }


}
