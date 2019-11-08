package edu.wofford.wocoin;

import edu.wofford.wocoin.gui.MainMenu;
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

    @Before
    public void setUp(){
        rebuildTestDB();
    }

    @After
    public void tearDown() {
        File file = new File("test.db");
        if (file.exists()) {
            boolean delete = file.delete();
        }
    }

    public void rebuildTestDB() {
        File file = new File("test.db");
        if (file.exists()) {
            boolean delete = file.delete();
        }

        Utilities.createNewDatabase("test.db");
    }

    @Test
    public void testAddUser() {
        ConsoleController cm = new ConsoleController(new SQLController("test.db"));
        cm.adminLogin("adminpwd");
        assertEquals(cm.addUser("testadduser", "test"), "testadduser was added.");
        assertEquals(cm.addUser("testadduser", "test"), "testadduser already exists.");
        rebuildTestDB();
    }

    @Test
    public void testRemoveUser() {
        ConsoleController cm = new ConsoleController(new SQLController("test.db"));
        cm.adminLogin("adminpwd");
        cm.addUser("testadduser", "test");
        assertEquals(cm.removeUser("testadduser"), "testadduser was removed.");
        assertEquals(cm.removeUser("testadduser"), "testadduser does not exist.");
        rebuildTestDB();
    }

    @Test
    public void testUserLogin() {
        SQLController sqlController = new SQLController("test.db");
        sqlController.insertUser("testlogin", "testpass");
        ConsoleController cm = new ConsoleController(sqlController);
        assertFalse(cm.userLogin("baduser", "badpass"));
        assertTrue(cm.userLogin("testlogin", "testpass"));
        cm.doLogout();
        rebuildTestDB();
    }

    @Test
    public void testAddWallet() {
        SQLController sqlController = new SQLController("test.db");
        sqlController.insertUser("testwallet", "testpass");
        ConsoleController cm = new ConsoleController(sqlController);
        cm.userLogin("testwallet", "testpass");
        assertEquals(cm.getCurrentUser(), "testwallet");
        rebuildTestDB();
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

        rebuildTestDB();
    }

    @Test
    public void testAddNewProduct() {
        SQLController sqlController = new SQLController("test.db");
        sqlController.insertUser("paul","Wofford1854");
        sqlController.insertUser("john","Wofford1854");
        sqlController.addWallet("john","j12345");
        ConsoleController cm = new ConsoleController(sqlController);

        cm.userLogin("paul", "Wofford1854");
        assertEquals(cm.addNewProduct("testitem","This is the description.", 20), "User has no wallet.");

        cm.userLogin("john", "Wofford1854");

        assertEquals(cm.addNewProduct("testitem", "testdescription", 20), "Product added.");
        // TODO Check that the item is in the database, use sqlController

        assertEquals(cm.addNewProduct("", "testdescription", 20), "Invalid value.\nExpected a string with at least 1 character.");
        assertEquals(cm.addNewProduct("testitem", "", 20), "Invalid value.\nExpected a string with at least 1 character.");
        assertEquals(cm.addNewProduct("testitem", "testdescription", 0), "Invalid value.\nExpected an integer value greater than or equal to 1.");
        assertEquals(cm.addNewProduct("testitem", "testdescription", -1), "Invalid value.\nExpected an integer value greater than or equal to 1.");
    }
}
