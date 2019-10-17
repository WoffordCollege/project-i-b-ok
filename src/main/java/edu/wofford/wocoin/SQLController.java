package edu.wofford.wocoin;
import java.sql.*;
import edu.wofford.wocoin.Utilities;

public class SQLController {

    private Connection dataConn;
    private String url;
    public enum sqlResult {ADDED, NOTADDED, DUPLICATE, REMOVED, NOTREMOVED, NORECORD}

    /**
     * Constructor that takes the name of the file
     * @param filename
     */
    public SQLController(String filename) {
        try{
            url = filename;
            dataConn = DriverManager.getConnection("jdbc:sqlite:" + filename);
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Constructor where the database defaults to wocoinDatabase.sqlite3
     */
    public SQLController() {
        try {
            url = "wocoinDatabase.sqlite3";
            dataConn = DriverManager.getConnection("jdbc:sqlite:wocoinDatabase.sqlite3");
        }
        catch(Exception e) {
            System.out.println("Error");
        }
    }

    /**
     * Closes the connection to the database
     */
    public void closeConnection(){
        try{
            dataConn.close();
        } catch(Exception e){
            System.out.println(e.toString());
        }
    }

    /**
     *
     * @return the path to the database
     */
    public String getPath(){
        return url;
    }

    /**
     *
     * @param name: the name of the user
     * @return true if the user has a recod in the table
     */
    public boolean lookupUser(String name){
        try{
            PreparedStatement stSelect = dataConn.prepareStatement("Select count(*) from users where id = ?");
            stSelect.setString(1,name);
            ResultSet dtr = stSelect.executeQuery();
            return dtr.getInt(1)>0;
        } catch(Exception e){
            System.out.println(e.toString());
        }
        return false;
    }

    /**
     *
     * @param name: the name of the user to be added
     * @param password: the associated password for the user
     * @return
     * if successful returns ADDED
     * if unsuccessful, returns why
     */
    public sqlResult insertUser(String name, String password){
        if(lookupUser(name)){
            return sqlResult.DUPLICATE;
        }
        
        try {
            PreparedStatement stInsert = dataConn.prepareStatement("INSERT INTO users (id, salt, hash) VALUES (?, ?, ?)");
            int salt = Utilities.generateSalt();
            String strHash = Utilities.applySha256(password+salt);

            stInsert.setString(1, name);
            stInsert.setInt(2, salt);
            stInsert.setString(3, strHash);

            stInsert.execute();
            return sqlResult.ADDED;
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }

        return sqlResult.NOTADDED;
    }

    /**
     *
     * @param name: the user to be removed
     * @return
     * if successful returns REMOVED
     * if unsuccessful, returns why
     */
    public sqlResult removeUser(String name){
        if(!lookupUser(name)){
            return sqlResult.NORECORD;
        }

        try {
            PreparedStatement stDelete = dataConn.prepareStatement("delete from users where id = ?");
            stDelete.setString(1, name);

            stDelete.execute();
            return sqlResult.REMOVED;
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }

        return sqlResult.NOTREMOVED;
    }

}
