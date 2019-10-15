package edu.wofford.wocoin;

import edu.wofford.wocoin.main.SQLController;
import org.junit.*;
import static org.junit.Assert.*;
import java.sql.*;

public class SQLControllerTest {

    @Test
    public final void testConstructor(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        assertEquals("wocoinDatabase.sqlite3", foo.getPath());
    }

    @Test
    public final void addUserTest(){
        SQLController foo = new SQLController("wocoinDatabase.sqlite3");
        SQLController.sqlResult tmp = foo.insertUser("Conner","password");
        assertEquals(SQLController.sqlResult.ADDED,tmp);
        try{
            Connection connWocoin = DriverManager.getConnection("wocoinDatabase.sqlite3");
            String cmdSelect = "select name from users";
            Statement stmSelect = connWocoin.createStatement();
            ResultSet dtr = stmSelect.executeQuery(cmdSelect);
            assertEquals("Conner",dtr.getString(1));
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }
}
