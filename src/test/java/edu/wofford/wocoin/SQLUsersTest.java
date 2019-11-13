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
            createUserTestDatabase("TestData.sqlite3");
            setupIsDone = true;
        }
        foo = new SQLController("TestData.sqlite3");
    }

    public static void createUserTestDatabase(String filename) {
        String url = "jdbc:sqlite:" + filename;
        String users = "CREATE TABLE IF NOT EXISTS users (" +
                "id text PRIMARY KEY, " +
                "salt integer NOT NULL, " +
                "hash text NOT NULL)";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String[] sqls = { "INSERT INTO users (id, salt, hash) VALUES (\"jdoe\", 13587, \"ebd3528832b124bb7886cd8e8d42871c99e06d5f3ad0c6ee883f6219b2b6a955\")",
                "INSERT INTO users (id, salt, hash) VALUES (\"jsmith\", 52196, \"9d3194cf601e62d35f144abebcea7704ad005402e102d134bd8f82ac469c2ec9\")",
                "INSERT INTO users (id, salt, hash) VALUES (\"hjones\", 47440, \"5d94ecaff496ac900a1f68ec950153aa1f500d06227b65167f460e5dd20a959b\")",
                "INSERT INTO users (id, salt, hash) VALUES (\"srogers\", 54419, \"26f2573d733da38fb3cd09eb79f884bbe63010570d394de7d8809b65823da85a\")",
        };

        try (Connection conn = DriverManager.getConnection(url)) {
            for (String sql : sqls) {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
                // Wait for one second so that timestamps are different.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @AfterClass
    public static void destroy(){
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
