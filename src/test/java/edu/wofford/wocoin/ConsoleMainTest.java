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
    public void setup() {
        String programInput = "1\n1\n1\n1\n";
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        String actualOutput;
        try {
            System.setIn(new ByteArrayInputStream(programInput.getBytes()));
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            ConsoleMain.main(args);
            actualOutput = outContent.toString();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        assertThat(actualOutput, containsString());
    }
}
