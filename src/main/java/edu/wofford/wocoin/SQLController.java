package edu.wofford.wocoin;
import java.sql.*;
import edu.wofford.wocoin.Utilities;

public class SQLController {

    private Connection dataConn;
    private String url;
    public enum sqlResult {ADDED, NOTADDED}

    public SQLController(String filename) {
        try{
            url = filename;
            dataConn = DriverManager.getConnection("jdbc:sqlite:" + filename);
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    public SQLController() {
        try {
            url = "wocoinDatabase.sqlite3";
            dataConn = DriverManager.getConnection("jdbc:sqlite:wocoinDatabase.sqlite3");
        }
        catch(Exception e) {
            System.out.println("Error");
        }
    }

    public String getPath(){
        return url;
    }

    public sqlResult insertUser(String name, String password){
        sqlResult retVal = sqlResult.NOTADDED;
        
        try {
            PreparedStatement stInsert = dataConn.prepareStatement("INSERT INTO users (id, salt, hash) VALUES (?, ?, ?)");
            int salt = Utilities.generateSalt();
            String strHash = Utilities.applySha256(password+salt);

            stInsert.setString(1, name);
            stInsert.setInt(2, salt);
            stInsert.setString(3, strHash);

            stInsert.execute();
            retVal = sqlResult.ADDED;
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }

        return retVal;
    }
}
