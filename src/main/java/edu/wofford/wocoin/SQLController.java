package edu.wofford.wocoin;
import java.io.File;
import java.sql.*;

public class SQLController {

    private String url;

    public enum AddUserResult {ADDED, DUPLICATE, NOTADDED}
    public enum RemoveUserResult {REMOVED, NORECORD, NOTREMOVED}
    public enum LoginResult{SUCCESS, NOSUCHUSER, WRONGPASSWORD, UNSET}
    public enum AddWalletResult {ADDED, ALREADYEXISTS, NOTADDED}
    public enum ReplaceWalletResult {REPLACED, NOTREPLACED, NOSUCHWALLET}
    public enum RemoveWalletResult {REMOVED, NOSUCHWALLET, NOTREMOVED}

    /**
     * Constructor that takes the name of the file
     * @param filename The name of the file.
     */
    public SQLController(String filename) {

        if (!new File(filename).exists()) {
            Utilities.createNewDatabase(filename);
        }
        url = "jdbc:sqlite:" + filename;
    }

    /**
     * Constructor where the database defaults to wocoinDatabase.sqlite3
     */
    public SQLController() {
        this("wocoinDatabase.sqlite3");
    }

    /**
     *Get the path to the database.
     * @return the path to the database
     */
    public String getPath(){
        return url;
    }

    /**
     *
     * @param name: the name of the user
     * @return true if the user has a record in the table
     */
    public boolean lookupUser(String name){
        boolean returnVal = false;
        try (Connection dataConn = DriverManager.getConnection(url)) {
            PreparedStatement stSelect = dataConn.prepareStatement("SELECT count(*) FROM users WHERE id = ?");
            stSelect.setString(1, name);
            ResultSet dtr = stSelect.executeQuery();
            returnVal = dtr.getInt(1) > 0;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return returnVal;
    }

    /**
     *
     * @param name: the name of the user to be added
     * @param password: the associated password for the user
     * @return
     * if successful returns ADDED
     * if duplicate username, returns DUPLICATE
     * if unsuccessful, returns NOTADDED
     */
    public AddUserResult insertUser(String name, String password){
        if(lookupUser(name)){
            return AddUserResult.DUPLICATE;
        }

        int salt = Utilities.generateSalt();
        String strHash = Utilities.applySha256(password + salt);

        AddUserResult result = AddUserResult.NOTADDED;

        try (Connection dataConn = DriverManager.getConnection(url)) {
            PreparedStatement stInsert = dataConn.prepareStatement("INSERT INTO users (id, salt, hash) VALUES (?, ?, ?)");
            stInsert.setString(1, name);
            stInsert.setInt(2, salt);
            stInsert.setString(3, strHash);

            stInsert.execute();
            dataConn.close();
            result = AddUserResult.ADDED;
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return result;
    }

    /**
     *
     * @param name: the user to be removed
     * @return
     * if successful returns REMOVED
     * if unsuccessful, returns why
     */
    public RemoveUserResult removeUser(String name){
        if(!lookupUser(name)){
            return RemoveUserResult.NORECORD;
        }

        RemoveUserResult result = RemoveUserResult.NOTREMOVED;

        try (Connection dataConn = DriverManager.getConnection(url)){
            PreparedStatement stDelete = dataConn.prepareStatement("DELETE FROM users WHERE id = ?");
            stDelete.setString(1, name);
            stDelete.execute();
            result = RemoveUserResult.REMOVED;
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }

        return result;
    }

    /**
     * Verifies that the password is associated with that user.
     * @param user The user name
     * @param password The password
     * @return 
     */
    public LoginResult userLogin(String user, String password){
        LoginResult retVal = LoginResult.UNSET;
        if(this.lookupUser(user)){
            try (Connection dataConn = DriverManager.getConnection(url)) {
                PreparedStatement stSelect = dataConn.prepareStatement("SELECT * FROM users WHERE id = ?");
                stSelect.setString(1, user);
                ResultSet dtr = stSelect.executeQuery();
                int salt = dtr.getInt(2);
                String hash = dtr.getString(3);
                String strHash = Utilities.applySha256(password + salt);
                if(hash.equals(strHash)){
                    retVal = LoginResult.SUCCESS;
                } else{
                    retVal = LoginResult.WRONGPASSWORD;
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        } else{
            retVal = LoginResult.NOSUCHUSER;
        }
        return retVal;
    }

    /**
     * Checks to see if the user has an associated wallet in the database.
     * @param user The user name to check for.
     * @return True if the user has a wallet
     */
    public boolean findWallet(String user){
        boolean retVal = false;
        try (Connection dataConn = DriverManager.getConnection(url)) {
            PreparedStatement stSelect = dataConn.prepareStatement("SELECT count(*) FROM wallets WHERE id = ?");
            stSelect.setString(1, user);
            ResultSet dtr = stSelect.executeQuery();
            retVal = dtr.getInt(1) > 0;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return retVal;
    }

    /**
     * This method takes a user name and the the public key of the user's wallet.
     * It then adds this to the wallets table.
     * @param user The user name to be added
     * @param pubKey the public key of the wallet
     * @return
     */
    public AddWalletResult addWallet(String user, String pubKey){
        AddWalletResult retVal;
        if(findWallet(user)){
            retVal = AddWalletResult.ALREADYEXISTS;
        } else {
            try (Connection dataConn = DriverManager.getConnection(url)) {
                PreparedStatement stInsert = dataConn.prepareStatement("Insert into wallets (id, publickey) values (?,?)");
                stInsert.setString(1, user);
                stInsert.setString(2, pubKey);
                stInsert.execute();
                retVal = AddWalletResult.ADDED;
            } catch (Exception e) {
                System.out.println(e.toString());
                retVal = AddWalletResult.NOTADDED;
            }
        }
        return retVal;
    }

    /**
     * This method is used to replace a wallet that already exists
     * @param user the user name associated with the wallet
     * @param pubKey the public key of the wallet
     * @return whether the wallet was successfully replaced or that the wallet did not exist
     */
    public ReplaceWalletResult replaceWallet(String user, String pubKey){
        ReplaceWalletResult retVal = ReplaceWalletResult.NOTREPLACED;
        if(findWallet(user)){
            try (Connection dataConn = DriverManager.getConnection(url)) {
                PreparedStatement stUpdate = dataConn.prepareStatement("Update wallets set publickey = ? WHERE id = ?");
                stUpdate.setString(1, user);
                stUpdate.setString(2, pubKey);
                stUpdate.execute();
                retVal = ReplaceWalletResult.REPLACED;
            } catch (Exception e) {
                System.out.println(e.toString());
                retVal = ReplaceWalletResult.NOTREPLACED;
            }
        } else{
            retVal = ReplaceWalletResult.NOSUCHWALLET;
        }

        return retVal;
    }

    /**
     * This method removes a wallet from the database
     * @param name the user name
     * @return whether the wallet was successfully removed or that the wallet did not exist
     */
    public RemoveWalletResult removeWallet(String name){
        if(!findWallet(name)){
            return RemoveWalletResult.NOSUCHWALLET;
        }

        RemoveWalletResult result = RemoveWalletResult.NOTREMOVED;

        try (Connection dataConn = DriverManager.getConnection(url)){
            PreparedStatement stDelete = dataConn.prepareStatement("DELETE FROM wallets WHERE id = ?");
            stDelete.setString(1, name);
            stDelete.execute();
            result = RemoveWalletResult.REMOVED;
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }

        return result;
    }
}
