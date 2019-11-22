package edu.wofford.wocoin;
import java.io.File;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;

/**
 * This class is an API used to connect to a Wocoin database.
 * It provides the functions necessary to populate and erase information from the database.
 */
public class SQLController {

    private String url;

    /**
     * An enumeration of the possible results of attempting to add a user
     */
    public enum AddUserResult {ADDED, DUPLICATE, NOTADDED}
    /**
     * An enumeration of the possible results of attempting to remove a user
     */
    public enum RemoveUserResult {REMOVED, NORECORD, NOTREMOVED}
    /**
     * An enumeration of the possible results of attempting to login as a user
     */
    public enum LoginResult{SUCCESS, NOSUCHUSER, WRONGPASSWORD, UNSET}
    /**
     * An enumeration of the possible results of attempting to add a wallet to a user
     */
    public enum AddWalletResult {ADDED, ALREADYEXISTS, NOTADDED}
    /**
     * An enumeration of the possible results of attempting to replace a user's wallet
     */
    public enum ReplaceWalletResult {REPLACED, NOTREPLACED, NOSUCHWALLET}
    /**
     * An enumeration of the possible results of attempting to remove a wallet from a user
     */
    public enum RemoveWalletResult {REMOVED, NOSUCHWALLET, NOTREMOVED}
    /**
     * An enumeration of the possible results of attempting to add a product to the DB
     */
    public enum AddProductResult {ADDED, NOTADDED, NOWALLET, EMPTYDESCRIPTION, EMPTYNAME, NONPOSITIVEPRICE}
    /**
     * An enumeration of the possible results of attempting to remove a product from the DB
     */
    public enum RemoveProductResult {REMOVED, NOTREMOVED, NOWALLET, DOESNOTEXIST}
    /**
     * An enumeration of the possible results of attempting to transfer Wocoins to a user
     */
    public enum TransferWocoinResult {SUCCESS, NOUSER, NOWALLET, NEGATIVEINPUT}
    /**
     * An enumeration of the possible results of attempting to send a message to a user
     */
    public enum SendMessageResult {SENT, INVALIDSENDER, INVALIDRECIPIENT, NOWALLET, NOTSENT}
    /**
     * An enumeration of the possible results of attempting to delete a message from a user
     */
    public enum DeleteMessageResult {DELETED, DOESNOTEXIST, NOTDELETED}



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
    String getPath(){
        return url;
    }

    /**
     * Queries the database to see if the user exists
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
     * Tries to add the user to the database
     * @param name: the name of the user to be added
     * @param password: the associated password for the user
     * @return
     * if successful returns ADDED
     * if duplicate username, returns DUPLICATE
     * if unsuccessful, returns NOTADDED
     */
    AddUserResult insertUser(String name, String password){
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
     * Removes the specified user from the database
     * @param name: the user to be removed
     * @return if successful returns REMOVED and if unsuccessful, returns why
     */
    RemoveUserResult removeUser(String name){
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
     * @return whether the user was successfully removed or that the user did not exist
     */
    LoginResult userLogin(String user, String password){
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
     * @return whether the wallet was successfully added or that the wallet already exists
     */
    AddWalletResult addWallet(String user, String pubKey){
        AddWalletResult retVal = AddWalletResult.NOTADDED;
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
    ReplaceWalletResult replaceWallet(String user, String pubKey){
        ReplaceWalletResult retVal = ReplaceWalletResult.NOTREPLACED;
        if(findWallet(user)){
            try (Connection dataConn = DriverManager.getConnection(url)) {
                PreparedStatement stUpdate = dataConn.prepareStatement("Update wallets set publickey = ? WHERE id = ?");
                stUpdate.setString(1, pubKey);
                stUpdate.setString(2, user);
                stUpdate.execute();
                retVal = ReplaceWalletResult.REPLACED;
            } catch (Exception e) {
                System.out.println(e.toString());
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
    RemoveWalletResult removeWallet(String name){
        if(!findWallet(name)){
            return RemoveWalletResult.NOSUCHWALLET;
        }

        RemoveWalletResult result = RemoveWalletResult.NOTREMOVED;

        try (Connection dataConn = DriverManager.getConnection(url)){
            PreparedStatement stDelete = dataConn.prepareStatement("DELETE FROM wallets WHERE id = ?");
            stDelete.setString(1, name);
            stDelete.execute();
            result = RemoveWalletResult.REMOVED;
        } catch(Exception e) {
            System.out.println(e.toString());
        }

        return result;
    }

    /**
     * Retrieves the public key from the database for the provided user name
     * @param user The name of the user
     * @return The public key or an empty string if the user is not in the database
     */
    String retrievePublicKey(String user){
        String retVal = "";
        try (Connection dataConn = DriverManager.getConnection(url)) {
            PreparedStatement stSelect = dataConn.prepareStatement("SELECT publickey FROM wallets WHERE id = ?");
            stSelect.setString(1, user);
            ResultSet dtr = stSelect.executeQuery();
            retVal = dtr.getString(1);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return retVal;
    }

    /**
     * Retrieves the user name from the database for the provided public key
     * @param publicKey The public key for the user
     * @return The user name or an empty string if the user is not in the database
     */
    String getName(String publicKey){
        String retVal = "";
        try (Connection dataConn = DriverManager.getConnection(url)) {
            PreparedStatement stSelect = dataConn.prepareStatement("SELECT id FROM wallets WHERE publickey = ?");
            stSelect.setString(1, publicKey);
            ResultSet dtr = stSelect.executeQuery();
            retVal = dtr.getString(1);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return retVal;
    }

    /**
     * Checks to see if the product exists in the database.
     * @param product The product to check for.
     * @return True if the product is in the database
     */
    public boolean productExistsInDatabase(Product product) {
        try (Connection dataConn = DriverManager.getConnection(url)) {
            PreparedStatement stSelect = dataConn.prepareStatement("SELECT COUNT(*) FROM products WHERE seller = ? AND price = ? AND name = ? AND description = ?");
            stSelect.setString(1, this.retrievePublicKey(product.getSeller()));
            stSelect.setInt(2, product.getPrice());
            stSelect.setString(3, product.getName());
            stSelect.setString(4, product.getDescription());
            ResultSet dtr = stSelect.executeQuery();
            return dtr.getInt(1) != 0;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return false;
    }

    /**
     * Adds a product to the database.
     * @param product the product to be added to the database.
     * @return Added if successful, else why the product was not added.
     */
    AddProductResult addProduct(Product product){
        AddProductResult retVal = AddProductResult.NOTADDED;

        if(!findWallet(product.getSeller())){
            retVal = AddProductResult.NOWALLET;
        } else if(product.getPrice() <= 0){
            retVal = AddProductResult.NONPOSITIVEPRICE;
        } else if(product.getName().trim().isEmpty()){
            retVal = AddProductResult.EMPTYNAME;
        } else if(product.getDescription().trim().isEmpty()){
            retVal = AddProductResult.EMPTYDESCRIPTION;
        } else {
            try (Connection dataConn = DriverManager.getConnection(url)) {
                PreparedStatement stInsert = dataConn.prepareStatement("Insert into products (seller, price, name, description) values (?,?,?,?)");
                stInsert.setString(1, this.retrievePublicKey(product.getSeller()));
                stInsert.setInt(2, product.getPrice());
                stInsert.setString(3, product.getName());
                stInsert.setString(4, product.getDescription());
                stInsert.execute();
                retVal = AddProductResult.ADDED;
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        return retVal;
    }

    /**
     * Removes a product from the database
     * @param productToRemove the product to be removed from the database.
     * @return Removed if successful, otherwise a {@link RemoveProductResult} describing the failure
     */
    RemoveProductResult removeProduct(Product productToRemove) {
        RemoveProductResult retval = RemoveProductResult.NOTREMOVED;

        if (!findWallet(productToRemove.getSeller())){
            retval = RemoveProductResult.NOWALLET;
        }
        else if (!this.productExistsInDatabase(productToRemove)) {
            retval = RemoveProductResult.DOESNOTEXIST;
        }
        else {
            try (Connection dataConn = DriverManager.getConnection(url)) {
                if (productToRemove.getId() < 0) {
                    PreparedStatement stSelect = dataConn.prepareStatement("DELETE FROM products WHERE seller = ? AND price = ? AND name = ? AND description = ? AND id = (SELECT max(id) from main.products a WHERE seller = a.seller AND price = a.price AND name = a.name AND description = a.description)");
                    stSelect.setString(1, productToRemove.getSeller());
                    stSelect.setInt(2, productToRemove.getPrice());
                    stSelect.setString(3, productToRemove.getName());
                    stSelect.setString(4, productToRemove.getDescription());
                    stSelect.execute();
                    retval = RemoveProductResult.REMOVED;
                }
                else {
                    PreparedStatement stSelect = dataConn.prepareStatement("DELETE FROM products WHERE id = ?");
                    stSelect.setInt(1, productToRemove.getId());
                    stSelect.execute();
                    retval = RemoveProductResult.REMOVED;
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return retval;
    }

    /**
     * Gets a list of the products in the database added by the username below
     * @param username The username of the user whose products are being retrieved
     * @return An ArrayList of the {@link Product} added by the user
     */
    public ArrayList<Product> getUserProductsList (String username) {
        ArrayList<Product> products = new ArrayList<>();
        try (Connection dataConn = DriverManager.getConnection(url)) {
            PreparedStatement stSelect = dataConn.prepareStatement("SELECT id, price, name, description, (SELECT id FROM wallets WHERE wallets.publickey = products.seller) user FROM products WHERE user = ?");
            stSelect.setString(1, username);
            createProductsListFromStatement(products, stSelect, Product.DisplayType.HIDECURRENTUSER);
        } catch (Exception e) {
            System.out.println("NOT_HERE" + e.toString());
        }
        return products;
    }

    /**
     * Gets a list of the products in the database not added by the username below and whose price is less than or equal to the user's wocoin balance
     * @param username The username of the user whose products are not being retrieved
     * @param balance the user's wocoin balance
     * @return An ArrayList of the {@link Product} that can be bought by the user
     */
    ArrayList<Product> getPurchasableProductsList (String username, int balance) {
        ArrayList<Product> products = new ArrayList<>();
        try (Connection dataConn = DriverManager.getConnection(url)) {
            PreparedStatement stSelect = dataConn.prepareStatement("SELECT id, price, name, description, (SELECT id FROM wallets WHERE wallets.publickey = products.seller) user FROM products WHERE user <> ? and price <= ?");
            stSelect.setString(1, username);
            stSelect.setInt(2, balance);
            createProductsListFromStatement(products, stSelect, Product.DisplayType.HIDECURRENTUSER);
        } catch (Exception e) {
            System.out.println("NOT_HERE" + e.toString());
        }
        return products;
    }

    /**
     * Gets a list of all of the products in the database
     * @return An ArrayList of the {@link Product} in the database
     */
    public ArrayList<Product> getAllProductsList () {
        ArrayList<Product> products = new ArrayList<>();
        try (Connection dataConn = DriverManager.getConnection(url)) {
            PreparedStatement stSelect = dataConn.prepareStatement("SELECT id, price, name, description, (SELECT id FROM wallets WHERE wallets.publickey = products.seller) user FROM products");
            createProductsListFromStatement(products, stSelect, Product.DisplayType.SHOWCURRENTUSER);
        } catch (Exception e) {
            System.out.println("NOT_HERE" + e.toString());
        }
        return products;
    }

    /**
     * Gets a list of a user's purchasable products in the database
     * @return An ArrayList of the {@link Product} in the database
     */
    ArrayList<Product> getPurchasableProducts (String username) {
        ArrayList<Product> products = new ArrayList<>();
        try (Connection dataConn = DriverManager.getConnection(url)) {
            PreparedStatement stSelect = dataConn.prepareStatement("SELECT id, price, name, description, (SELECT id FROM wallets WHERE wallets.publickey = products.seller) user FROM products WHERE user <> ?");
            stSelect.setString(1, this.retrievePublicKey(username));
            createProductsListFromStatement(products, stSelect, Product.DisplayType.SHOWCURRENTUSER);
        } catch (Exception e) {
            System.out.println("NOT_HERE" + e.toString());
        }
        return products;
    }

    /**
     * This function takes a prepared statement and creates a list of products from the database result
     * @param products the {@link ArrayList} of products to be appended to
     * @param stSelect the select statement to use to connect to the DB
     * @param displayType the Display Type of the products to be added
     * @throws SQLException if the statement is invalid
     */
    private void createProductsListFromStatement(ArrayList<Product> products, PreparedStatement stSelect, Product.DisplayType displayType) throws SQLException {
        ResultSet dtr = stSelect.executeQuery();
        while (dtr.next()) {
            Product newProduct = new Product(dtr.getInt("id"), dtr.getString("user"),
                    dtr.getInt("price"), dtr.getString("name"),
                    dtr.getString("description"));
            newProduct.setDisplayType(displayType);
            products.add(newProduct);
        }
    }

    /**
     * Add Wocoins to a specific user's wallet.
     * @param username the name of the user
     * @param amt the amount to be transferred
     * @return a TransferWocoinResult
     */
    TransferWocoinResult transferWocoin(String username, int amt) {
        if(!lookupUser(username)){
            return TransferWocoinResult.NOUSER;
        } else if (!findWallet(username)){
            return TransferWocoinResult.NOWALLET;
        } else if (amt <= 0){
            return TransferWocoinResult.NEGATIVEINPUT;
        } else {
            // TODO do blockchain transaction stuff
            return TransferWocoinResult.SUCCESS;
        }

    }

    /**
     * This function takes a username and returns an {@link ArrayList} of the {@link Message}s sent to the user.
     * The Messages returned are fully populated (id, recipient, sender, message, product)
     * @param username the username of the user receiving messages
     * @return an ArrayList of fully populated {@link Message}s
     */
    ArrayList<Message> getMessagesForUser(String username) {
        ArrayList<Message> messages = new ArrayList<>();
        try (Connection dataConn = DriverManager.getConnection(url)) {
            PreparedStatement stSelect = dataConn.prepareStatement("select a.id, (select id from wallets where publickey = a.sender) senderUserName, (select id from wallets where publickey = a.recipient) recieverUserName, message, b.id, b.price, b.name, b.description, a.dt from messages a join products b on a.productid = b.id;");
            ResultSet dtr = stSelect.executeQuery();
            while (dtr.next()) {
                Product newProduct = new Product(dtr.getInt(5),dtr.getString("senderUserName"),dtr.getInt(6),dtr.getString(7),dtr.getString(8));
                Message newMessage = new Message(dtr.getInt(1), dtr.getString("senderUserName"),dtr.getString("message"),dtr.getString(9),newProduct);
                newMessage.setRecipient(dtr.getString("recieverUserName"));
                messages.add(newMessage);
            }
        } catch (Exception e) {
            System.out.println("NOT_HERE" + e.toString());
        }
        // TODO Get messages from DB ordered by submitDateTime where newer messages are first
        return messages;
    }

    /**
     * This function takes in a {@link Message} object and adds it to the database. After adding it to the database,
     * it returns a {@link SendMessageResult} with the action that occurred upon adding it.
     * @param message the message to be sent
     * @return a {@link SendMessageResult} with the result of attempting to send the message.
     */
    SendMessageResult sendMessage(Message message) {
        // TODO Check to make sure sender and recipient have a wallet
        // TODO Add the message to the database
        return SendMessageResult.NOTSENT;
    }

    /**
     * This function takes in a {@link Message} object and deletes it from the database.
     * It returns a {@link DeleteMessageResult} with the result of attempting to delete it from the Database.
     * @param message the message to be deleted from the database.
     * @return a {@link DeleteMessageResult} with the result of deleting it from the database.
     */
    DeleteMessageResult deleteMessage(Message message) {
        // STUB
        // TODO check if the message exists, and, if so, delete it
        return DeleteMessageResult.NOTDELETED;
    }

    /**
     * This function takes a username and returns the balance of their wallet.
     * If the user does not exist, returns null
     * If no wallet exists, returns -1
     * @param username the username of the wallet owner
     * @return a {@link BigInteger} denoting the balance of the user's wallet
     */
    BigInteger getUserBalance(String username) {
        if (!lookupUser(username)) {
            return null;
        }
        else if (!findWallet(username)) {
            return BigInteger.valueOf(-1);
        }
        else {
            return Utilities.getBalance(this.retrievePublicKey(username));
        }
    }
}
