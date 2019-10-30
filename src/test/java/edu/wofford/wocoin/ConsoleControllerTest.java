package edu.wofford.wocoin;

import edu.wofford.wocoin.main.ConsoleMain;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class ConsoleControllerTest {
    public final String loginScreenString = "1: exit\n2: administrator\n3: user";

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
        assertEquals("1: exit\n2: administrator\n3: user\n", output);
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

    @Test
    public void testUIStateString() {
        SQLController sqlController = new SQLController("test.db");
        sqlController.insertUser("testuser", "testpass");
        ConsoleController cm = new ConsoleController(sqlController);
        assertEquals(cm.getCurrentUIString(), loginScreenString);
        cm.adminLogin("badpass");
        assertEquals(cm.getCurrentUIString(), loginScreenString);
        cm.adminLogin("adminpwd");
        assertEquals(cm.getCurrentUIString(), "1: back\n2: add user\n3: remove user");
        cm.doLogout();
        assertEquals(cm.getCurrentUIString(), loginScreenString);
        cm.userLogin("testuser", "testpass");
        assertEquals(cm.getCurrentUIString(), "1: back\n2: create wallet");
        cm.doLogout();
        assertEquals(cm.getCurrentUIString(), loginScreenString);
        cm.exit();
        assertEquals(cm.getCurrentUIString(), "");
    }

    @Test
    public void testAdminLogin() {
        ConsoleController cm = new ConsoleController(new SQLController("test.db"));
        assertEquals(cm.getCurrentState(), ConsoleController.UIState.LOGIN);
        assertFalse(cm.adminLogin("badpass"));
        assertEquals(cm.getCurrentState(), ConsoleController.UIState.LOGIN);
        assertTrue(cm.adminLogin("adminpwd"));
        assertEquals(cm.getCurrentState(), ConsoleController.UIState.ADMINISTRATOR);

        assertTrue(cm.adminLogin("adminpwd"));
        assertEquals(cm.getCurrentState(), ConsoleController.UIState.ADMINISTRATOR);
        cm.doLogout();
        assertEquals(cm.getCurrentState(), ConsoleController.UIState.LOGIN);
        cm.exit();
        assertEquals(cm.getCurrentState(), ConsoleController.UIState.EXIT);
    }

    @Test
    public void testAddUser() {
        ConsoleController cm = new ConsoleController(new SQLController("test.db"));
        assertNull(cm.addUser("testadduser", "test"));
        cm.adminLogin("adminpwd");
        assertEquals(cm.addUser("testadduser", "test"), "testadduser was added.");
        assertEquals(cm.addUser("testadduser", "test"), "testadduser already exists.");
    }

    @Test
    public void testRemoveUser() {
        ConsoleController cm = new ConsoleController(new SQLController("test.db"));
        assertNull(cm.removeUser("testadduser"));
        cm.adminLogin("adminpwd");
        cm.addUser("testadduser", "test");
        assertEquals(cm.removeUser("testadduser"), "testadduser was removed.");
        assertEquals(cm.removeUser("testadduser"), "testadduser does not exist.");
    }

    @Test
    public void testUserLogin() {
        SQLController sqlController = new SQLController("test.db");
        sqlController.insertUser("testlogin", "testpass");
        ConsoleController cm = new ConsoleController(sqlController);
        assertFalse(cm.userLogin("baduser", "badpass"));
        assertEquals(cm.getCurrentState(), ConsoleController.UIState.LOGIN);
        assertTrue(cm.userLogin("testlogin", "testpass"));
        assertEquals(cm.getCurrentState(), ConsoleController.UIState.USER);
        assertTrue(cm.userLogin("testlogin", "testpass"));
        assertEquals(cm.getCurrentState(), ConsoleController.UIState.USER);
        cm.doLogout();
        assertEquals(cm.getCurrentState(), ConsoleController.UIState.LOGIN);
    }

    @Test
    public void testAddWallet() {
        SQLController sqlController = new SQLController("test.db");
        sqlController.insertUser("testwallet", "testpass");
        ConsoleController cm = new ConsoleController(sqlController);
        cm.userLogin("testwallet", "testpass");
        assertEquals(cm.getCurrentState(), ConsoleController.UIState.USER);
        assertEquals(cm.getCurrentUser(), "testwallet");
    }

    @Test
    public void testUserWithWallet() {
        SQLController sqlController = new SQLController("test.db");
        sqlController.insertUser("testuserwithwallet", "testpassword");
        ConsoleController cm = new ConsoleController(sqlController);
        assertFalse(cm.userHasWallet());
        assertTrue(cm.userLogin("testuserwithwallet", "testpassword"));
        assertFalse(cm.userHasWallet());
        sqlController.addWallet("testuserwithwallet", "testkey");
        assertTrue(cm.userHasWallet());
        assertTrue(cm.deleteUserWallet());
        cm.doLogout();
        cm.removeUser("testuserwithwallet");
    }

    @Test
    public void testWalletCreation() {
        SQLController sqlController = new SQLController("test.db");
        sqlController.insertUser("testwalletcreate", "test");
        ConsoleController cm = new ConsoleController(sqlController);
        assertSame(WalletUtilities.CreateWalletResult.FAILED, cm.addWalletToUser("nouser"));
        assertTrue(cm.userLogin("testwalletcreate", "test"));
        assertSame(WalletUtilities.CreateWalletResult.SUCCESS, cm.addWalletToUser("test/"));
        assertSame(WalletUtilities.CreateWalletResult.FILEALREADYEXISTS, cm.addWalletToUser("test/"));
        assertSame(WalletUtilities.CreateWalletResult.SUCCESS, cm.addWalletToUser("test/test/"));
        assertTrue(sqlController.findWallet("testwalletcreate"));

        try {
            FileUtils.deleteDirectory(new File("test/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
