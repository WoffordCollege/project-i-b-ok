package edu.wofford.wocoin;

import edu.wofford.wocoin.main.Main;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;

public class ConsoleMainTest {

    private String sendProgramInput(String input) {
        String actualOutput = null;
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            ConsoleMain.main(new String[0]);
            actualOutput = outContent.toString();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        return actualOutput;
    }

    @Test
    public void testLoginScreen() {
        String output = sendProgramInput("1");
        assertEquals("1: exit\n2: administrator\n", output);
    }


    @Test
    public void testAdministratorScreen() {
        String output = sendProgramInput("2\nadminpwd\n1\n1");
        String expectedOutput = "1: back\n2: add user\n3: remove user";
        assertThat(output, containsString(expectedOutput));
    }

}
