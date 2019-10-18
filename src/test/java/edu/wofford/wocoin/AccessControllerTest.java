package edu.wofford.wocoin;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class AccessControllerTest implements UIController {

    private AccessController ac;

    private AccessController.Result result;
    private AccessController.AccessOptions[] accessOptions;

    private String[] adminLogin = {"", "adminpwd"};

    @Before
    public void setup() {
        ac = new AccessController(this);
    }

    @Test
    public void testAdminLogin() {
        ac.login(adminLogin[0], adminLogin[1]);
        AccessController.AccessOptions[] compareArray = new AccessController.AccessOptions[2];
        compareArray[0] = AccessController.AccessOptions.ADDUSER;
        compareArray[1] = AccessController.AccessOptions.DELETEUSER;
        assertEquals(AccessController.Result.SUCCESS, result);
        assertArrayEquals(compareArray, accessOptions);
        ac.login("", "notadminpwd");
        compareArray = new AccessController.AccessOptions[0];
        assertEquals(AccessController.Result.WRONG_PASSWORD, result);
        assertArrayEquals(compareArray, accessOptions);
    }

    @Test
    public void testAddUser() {

        try {
            Connection dataConn = DriverManager.getConnection("jdbc:sqlite:wocoinDatabase.sqlite3");
            PreparedStatement stDelete = dataConn.prepareStatement("delete from users where id = ?");
            stDelete.setString(1, "testuser");
            stDelete.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ac.login(adminLogin[0], adminLogin[1]);
        AccessController.AccessOptions[] compareArray = new AccessController.AccessOptions[2];
        compareArray[0] = AccessController.AccessOptions.ADDUSER;
        compareArray[1] = AccessController.AccessOptions.DELETEUSER;
        assertEquals(AccessController.Result.SUCCESS, result);
        assertArrayEquals(compareArray, accessOptions);

        ac.addUser("testuser", "testpassword");
        assertEquals(AccessController.Result.SUCCESS, result);
        assertTrue(new SQLController().lookupUser("testuser"));
        assertArrayEquals(compareArray, accessOptions);

        ac.addUser("testuser", "tst");
        assertEquals(AccessController.Result.INVALID_USERNAME, result);
        assertTrue(new SQLController().lookupUser("testuser"));
        assertArrayEquals(compareArray, accessOptions);

    }


    @Test
    public void testRemoveUser() {

        try {
            Connection dataConn = DriverManager.getConnection("jdbc:sqlite:wocoinDatabase.sqlite3");
            PreparedStatement stDelete = dataConn.prepareStatement("delete from users where id = 'testuser'");
            PreparedStatement stInsert = dataConn.prepareStatement("INSERT INTO users (id, salt, hash) VALUES ('testuser', 1, 'salt')");
            stDelete.execute();
            stInsert.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ac.login(adminLogin[0], adminLogin[1]);
        AccessController.AccessOptions[] compareArray = new AccessController.AccessOptions[2];
        compareArray[0] = AccessController.AccessOptions.ADDUSER;
        compareArray[1] = AccessController.AccessOptions.DELETEUSER;
        assertEquals(AccessController.Result.SUCCESS, result);
        assertArrayEquals(compareArray, accessOptions);

        //ac.addUser("testuser", "testPWD");

        ac.removeUser("notindb");
        assertEquals(AccessController.Result.INVALID_USERNAME, result);
        assertTrue(new SQLController().lookupUser("testuser"));
        assertArrayEquals(compareArray, accessOptions);

        ac.removeUser("testuser");
        assertEquals(AccessController.Result.SUCCESS, result);
        assertFalse(new SQLController().lookupUser("testuser"));
        assertArrayEquals(compareArray, accessOptions);

        ac.removeUser("testuser");
        assertEquals(AccessController.Result.INVALID_USERNAME, result);
        assertFalse(new SQLController().lookupUser("testuser"));
        assertArrayEquals(compareArray, accessOptions);
    }

    @Override
    public void updateDisplay(AccessController.Result actionResult, AccessController.AccessOptions[] userOptions) {
        this.result = actionResult;
        this.accessOptions = userOptions;
    }

    @Override
    public void updateDisplay(AccessController.Result actionResult, AccessController.AccessOptions[] userOptions, String[] args) {
        this.updateDisplay(actionResult, accessOptions);
    }
}
