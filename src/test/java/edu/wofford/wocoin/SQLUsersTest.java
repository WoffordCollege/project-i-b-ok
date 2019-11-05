package edu.wofford.wocoin;

import org.junit.*;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.*;


public class SQLUsersTest {

    private SQLController foo;
    private static boolean setupIsDone = false;

    @Before
    public void setUp(){
        if(!setupIsDone) {
            Utilities.createTestDatabase("TestData.sqlite3");
            setupIsDone = true;
        }
        foo = new SQLController("TestData.sqlite3");
    }

    @AfterClass
    public static void destroy(){
        System.out.println("SelfDestruct");
        new File("TestData.sqlite3").delete();
        new File("notadb.sqlite3").delete();
        new File("testDB.sqlite3").delete();
    }

    @Test
    public final void lookupUser(){
        assertTrue(foo.lookupUser("srogers"));
    }

    @Test
    public final void lookupNonExistantUser(){
        assertTrue(!foo.lookupUser("nonuser"));
    }

    @Test
    public final void addUserTest(){
        SQLController.AddUserResult insertUserResult = foo.insertUser("tstark","password");
        assertEquals(SQLController.AddUserResult.ADDED, insertUserResult);
        assertTrue(foo.lookupUser("tstark"));
    }

    @Test
    public final void removeUserTest(){
        SQLController.RemoveUserResult tmp = foo.removeUser("hjones");
        assertEquals(SQLController.RemoveUserResult.REMOVED, tmp);
        assertFalse(foo.lookupUser("hjones"));
    }

    @Test
    public final void removeNonExistantUser(){
        assertFalse(foo.lookupUser("hjones"));
        SQLController.RemoveUserResult tmp = foo.removeUser("hjones");
        assertEquals(SQLController.RemoveUserResult.NORECORD, tmp);
    }

    @Test
    public final void duplicateUserTestDiffPass(){
        assertTrue(foo.lookupUser("tstark"));
        SQLController.AddUserResult tmp = foo.insertUser("tstark","password1");
        assertEquals(SQLController.AddUserResult.DUPLICATE, tmp);
    }

    @Test
    public final void duplicateUserTestSamePass(){
        assertTrue(foo.lookupUser("tstark"));
        SQLController.AddUserResult tmp = foo.insertUser("tstark","password");
        assertEquals(SQLController.AddUserResult.DUPLICATE, tmp);
    }

    @Test
    public final void testExceptionsInFunctions() {
        SQLController badDBConnect = new SQLController("notadb.sqlite3");
        badDBConnect.lookupUser("testuser");
        badDBConnect.insertUser("testuser", "testpw");
        badDBConnect.removeUser("testuser");
    }

    @Test
    public final void userLoginSuccess(){
        assertFalse(foo.lookupUser("testUser"));
        foo.insertUser("testUser","asecret");
        assertEquals(SQLController.LoginResult.SUCCESS, foo.userLogin("testUser","asecret"));
    }

    @Test
    public final void userLoginWrongPassword(){
        foo.insertUser("testUserA","1");
        assertEquals(SQLController.LoginResult.WRONGPASSWORD, foo.userLogin("testUserA","x"));
    }

    @Test
    public final void userLoginInvalidUser(){
        foo.removeUser("testUser");
        assertEquals(SQLController.LoginResult.NOSUCHUSER, foo.userLogin("testUser","asecret"));
    }

}
