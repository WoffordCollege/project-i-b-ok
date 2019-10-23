package edu.wofford.wocoin;

import org.junit.*;
import static org.junit.Assert.*;
import java.sql.*;

public class SQLControllerTest {

    @Test
    public final void testConstructor(){
        SQLController foo = new SQLController("testDB.sqlite3");
        assertEquals("jdbc:sqlite:testDB.sqlite3", foo.getPath());

        foo = new SQLController();
        assertEquals("jdbc:sqlite:wocoinDatabase.sqlite3", foo.getPath());
    }

    @Test
    public final void lookupUser(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.removeUser("Marshall");
        foo.insertUser("Marshall","password");
        assertTrue(foo.lookupUser("Marshall"));
    }

    @Test
    public final void addUserTest(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");

        foo.removeUser("Connor");

        SQLController.AddUserResult insertUserResult = foo.insertUser("Connor","password");
        assertEquals(SQLController.AddUserResult.ADDED, insertUserResult);

        try {
            Connection connWocoin = DriverManager.getConnection("jdbc:sqlite:wocoinDatabase.sqlite3");
            String cmdSelect = "SELECT Count(*) FROM users WHERE id = 'Connor'";
            Statement stmSelect = connWocoin.createStatement();
            ResultSet dtr = stmSelect.executeQuery(cmdSelect);
            assertEquals(1,dtr.getInt(1));
            connWocoin.close();
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }

    @Test
    public final void removeUserTest(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");

        foo.insertUser("Connor","password");

        SQLController.RemoveUserResult tmp = foo.removeUser("Connor");
        assertEquals(SQLController.RemoveUserResult.REMOVED, tmp);

        try (Connection connWocoin = DriverManager.getConnection("jdbc:sqlite:wocoinDatabase.sqlite3");){

            String cmdSelect = "SELECT Count(*) FROM users WHERE id = 'Connor'";
            Statement stmSelect = connWocoin.createStatement();
            ResultSet dtr = stmSelect.executeQuery(cmdSelect);
            assertEquals(0, dtr.getInt(1));
            connWocoin.close();
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }

    @Test
    public final void duplicateUserTestDiffPass(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.removeUser("Garrett");
        foo.insertUser("Garrett","password");
        SQLController.AddUserResult tmp = foo.insertUser("Garrett","password1");
        assertEquals(SQLController.AddUserResult.DUPLICATE, tmp);
    }

    @Test
    public final void duplicateUserTestSamePass(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.removeUser("Garrett");
        foo.insertUser("Garrett","password");
        SQLController.AddUserResult tmp = foo.insertUser("Garrett","password");
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
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.removeUser("testUser");
        foo.insertUser("testUser","asecret");
        assertEquals(SQLController.LoginResult.SUCCESS, foo.userLogin("testUser","asecret"));
    }

    @Test
    public final void userLoginWrongPassword(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.removeUser("testUser");
        foo.insertUser("testUser","asecret");
        assertEquals(SQLController.LoginResult.WRONGPASSWORD, foo.userLogin("testUser","x"));
    }

    @Test
    public final void userLoginInvalidUser(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.removeUser("testUser");
        assertEquals(SQLController.LoginResult.NOSUCHUSER, foo.userLogin("testUser","asecret"));
    }

    @Test
    public final void walletExists(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        //foo.insertUser("testUser","testing");
        assertTrue(foo.findWallet("srogers"));
    }

    @Test
    public final void walletNotExists(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        assertTrue(!foo.findWallet("tstark"));
    }

    @Test
    public final void addWallet(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.removeWallet("test");
        assertEquals(SQLController.AddWalletResult.ADDED, foo.addWallet("test","8675309"));
    }

    @Test
    public final void addWalletDuplicate(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.addWallet("test","8675309");
        assertEquals(SQLController.AddWalletResult.ALREADYEXISTS, foo.addWallet("test","8675309"));
    }

    @Test
    public final void replaceWallet(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.addWallet("test","8675309");
        assertEquals(SQLController.ReplaceWalletResult.REPLACED, foo.replaceWallet("test","867530"));
    }

    @Test
    public final void replaceNonExistentWallet(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        assertEquals(SQLController.ReplaceWalletResult.NOSUCHWALLET, foo.replaceWallet("tstark","867530"));
    }

    @Test
    public final void removeWallet(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.addWallet("bbanner","q8675309");
        assertEquals(SQLController.RemoveWalletResult.REMOVED,foo.removeWallet("bbanner"));
    }

    @Test
    public final void removeNonExistentWallet(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.removeWallet("bbanner");
        assertEquals(SQLController.RemoveWalletResult.NOSUCHWALLET,foo.removeWallet("bbanner"));
    }

}
