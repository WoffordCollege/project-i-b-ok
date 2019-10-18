package edu.wofford.wocoin;

import org.junit.*;
import static org.junit.Assert.*;
import java.sql.*;

public class SQLControllerTest {

    @Test
    public final void testConstructor(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        assertEquals("wocoinDatabase.sqlite3", foo.getPath());
        foo.closeConnection();
    }

    @Test
    public final void lookupUser(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.removeUser("Marshall");
        foo.insertUser("Marshall","password");
        assertTrue(foo.lookupUser("Marshall"));
        foo.closeConnection();
    }

    @Test
    public final void addUserTest(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");

        foo.removeUser("Connor");

        SQLController.sqlResult tmp = foo.insertUser("Connor","password");
        assertEquals(SQLController.sqlResult.ADDED,tmp);
        foo.closeConnection();
        try {
            Connection connWocoin = DriverManager.getConnection("jdbc:sqlite:wocoinDatabase.sqlite3");
            String cmdSelect = "select Count(*) from users where id = 'Connor'";
            Statement stmSelect = connWocoin.createStatement();
            ResultSet dtr = stmSelect.executeQuery(cmdSelect);
            assertEquals(1,dtr.getInt(1));
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
        SQLController.sqlResult tmp = foo.insertUser("Garrett","password1");
        assertEquals(SQLController.sqlResult.DUPLICATE, tmp);
        foo.closeConnection();
    }

    @Test
    public final void duplicateUserTestSamePass(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        foo.removeUser("Garrett");
        foo.insertUser("Garrett","password");
        SQLController.sqlResult tmp = foo.insertUser("Garrett","password");
        assertEquals(SQLController.sqlResult.DUPLICATE, tmp);
        foo.closeConnection();
    }


}
