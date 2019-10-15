package edu.wofford.wocoin.main;
import java.sql.*;
import edu.wofford.wocoin.Utilities;

public class SQLController {

    private Connection dataConn;
    private String url;
    public enum sqlResult {ADDED, NOTADDED}

    public SQLController(String filename){
        try{
            url = filename;
            dataConn = DriverManager.getConnection("jdbc:sqlite:" + filename);
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }

    public SQLController(){
        try{
            url = "wocoindDatabase.sqlite3";
            dataConn = DriverManager.getConnection("jdbc:sqlite:wocoinDatabase.sqlite3");
        }catch(Exception e){
            System.out.println("Error");
        }
    }

    public String getPath(){
        return url;
    }

    public sqlResult insertUser(String name, String password){
        sqlResult retVal = sqlResult.NOTADDED;
        try{
            Statement stInsert = dataConn.createStatement();
            int salt = Utilities.generateSalt();
            String strHash = Utilities.applySha256(password+salt);
            String cmdInsert = "INSERT INTO users (id, salt, hash) VALUES (\""+name+"\", "+salt+", \""+strHash+"\")";
            stInsert.execute(cmdInsert);
            retVal = sqlResult.ADDED;
        }catch(Exception e){
            System.out.println(e.toString());
        }

        return retVal;
    }
}
