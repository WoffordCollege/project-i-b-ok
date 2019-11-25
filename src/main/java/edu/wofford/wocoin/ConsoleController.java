package edu.wofford.wocoin;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * This class stores the helper methods that can be used to create a Console style GUI
 * It stores a SQLController which is used to perform the necessary operations on the database
 */
public class ConsoleController {

    private SQLController sqlController;

    private String currentUser;
    private String currentPassword;

    /**
     * Constructs a new ConsoleController with the given {@link SQLController}. Sets the current state of the UI to Login
     * @param sqlController The {@link SQLController} with a given WocoinDatabase that is modified with the Controller
     */
    public ConsoleController(SQLController sqlController) {
        this.sqlController = sqlController;
        this.currentUser = null;
    }

    /**
     * This function updates the state of the display to Administrator if the input password is correct.
     * returns true if the UIState is already administrator, or if the password is correct
     * @param password this is the password the user uses to attempt to login as administrator
     * @return true if the UIState is already administrator or if the password is correct, and returns false otherwise
     */
    public boolean adminLogin(String password) {
        return password.equals("adminpwd");
    }

    /**
     * This function updates the state of the display to User if the username and password combination is correct.
     * If the login is successful, we store the username of the current user for wallet interaction
     * returns true if the username and password combination is correct.
     * returns false otherwise
     * @param username the username of the user logging in
     * @param password the password of the user logging in
     * @return true if the username and password combination is correct and false otherwise.
     */
    public boolean userLogin(String username, String password) {
        if (sqlController.userLogin(username, password) == SQLController.LoginResult.SUCCESS) {
            this.currentUser = username;
            this.currentPassword = password;
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * This function returns the current SQLController
     * @return the current sqlController
     */
    public SQLController getSqlController() {
        return sqlController;
    }

    /**
     * This is a helper method for use in checking if a user has a Wocoin wallet.
     * @return if there is a currentUser and they have a wallet, returns true, else false
     */
    public boolean userHasWallet() {
        return this.currentUser != null && sqlController.findWallet(this.currentUser);
    }

    /**
     * Deletes the Wocoin wallet of the currently logged in user. If there is no user logged in, returns false
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean deleteUserWallet() {
        return currentUser != null && sqlController.removeWallet(currentUser) == SQLController.RemoveWalletResult.REMOVED;
    }

    /**
     * This function takes the currently logged in user and generates a Wallet file (see {@link WalletUtilities})
     * at the given filepath combined with the username. (if filepath=tmp, then generated directory is tmp/currentuser)
     * If no user is logged in, returns failed, otherwise returns the result from {@link WalletUtilities}
     * @param filepath the main directory in which a subdirectory with the current user can be placed
     * @return a {@link WalletUtilities.CreateWalletResult} relating to the result of the executed operation.
     * @see WalletUtilities
     */
    public WalletUtilities.CreateWalletResult addWalletToUser(String filepath) {
        if (currentUser == null || currentUser.length() == 0) {
            return WalletUtilities.CreateWalletResult.FAILED;
        }
        Pair<String, WalletUtilities.CreateWalletResult> keyAndResult = WalletUtilities.createWallet(filepath, this.currentUser, this.currentPassword);
        if (keyAndResult.getSecond() == WalletUtilities.CreateWalletResult.SUCCESS) {
            if (sqlController.findWallet(this.currentUser)){
                sqlController.replaceWallet(this.currentUser, keyAndResult.getFirst());
            }
            else {
                sqlController.addWallet(this.currentUser, keyAndResult.getFirst());
            }
        }
        return keyAndResult.getSecond();
    }

    /**
     * Sets the current state of the UI to login
     */
    public void doLogout() {
        this.currentUser = null;
        this.currentPassword = null;
    }


    /**
     * This function takes a username and password and attempts to add it to the current database.
     * It returns a string indicating the action that occurred.
     * If the current state is not administrator, returns null.
     * If the user was added, returns a String in the form "username was added."
     * If the user was a duplicate, returns a String in the form "username already exists."
     * If an exception occurs, returns a String in the form "username was not added."
     * @param username The username of the user to be added
     * @param password The password of the user to be added
     * @return A string representing the action that occurred when the user was added.
     */
    public String addUser(String username, String password) {
        SQLController.AddUserResult result = sqlController.insertUser(username, password);

        switch (result) {
            case ADDED:
                return username + " was added.";
            case DUPLICATE:
                return username + " already exists.";
            default:
                return username + " was not added.";
        }
    }


    /**
     * This function takes a username and attempts to remove it from the current database.
     * It returns a string indicating the action that occurred.
     * If the current state is not administrator, returns null.
     * If the user was removed, returns a String in the form "username was removed."
     * If the user does not exist, returns a String in the form "username does not exist."
     * If an exception occurs, returns a String in the form "username was not removed."
     * @param username The username of the user to be added
     * @return A string representing the action that occurred when the user was added.
     */
    public String removeUser(String username) {
        SQLController.RemoveUserResult result = sqlController.removeUser(username);

        switch (result) {
            case REMOVED:
                return username + " was removed.";
            case NORECORD:
                return username + " does not exist.";
            default:
                return username + " was not removed.";
        }
    }

    /**
     * This can be used to determine who is the currently logged in user
     * @return the username of the user that is currently logged in, null if no one is logged in
     */
    public String getCurrentUser() {
        return currentUser;
    }

    /**
     * This function creates a new {@link Product} and adds it to the SQL database.
     * If the Product was successfully added, returns "Product added."
     * If the User has no wallet, returns "User has no wallet."
     * If the price is invalid, returns "Invalid value.\nExpected an integer value greater than or equal to 1."
     * If the description or name is not specified, returns "Invalid value.\nExpected a string with at least 1 character."
     * @param name The name of the new Product
     * @param description The description of the new Product
     * @param price The price of the new Product
     * @return A String representing the result of adding the Product to the DB
     */
    public String addNewProduct(String name, String description, int price) {
        Product newProduct = new Product(this.currentUser, price, name, description);
        SQLController.AddProductResult result = sqlController.addProduct(newProduct);
        switch (result) {
            case ADDED:
                return "Product added.";
            case NOWALLET:
                return "User has no wallet.";
            case NONPOSITIVEPRICE:
                return "Invalid value.\nExpected an integer value greater than or equal to 1.";
            default:
                return "Invalid value.\nExpected a string with at least 1 character.";
        }
    }

    /**
     * This function returns an ArrayList of the current user's products
     * @return An arraylist of the current user's products
     * @see SQLController#getUserProductsList(String)
     */
    public ArrayList<Product> getUserProducts() {
        return sqlController.getUserProductsList(this.currentUser);
    }


    /**
     * This function returns an ArrayList of all of the products in the database
     * @return An arraylist of all of the products
     * @see SQLController#getAllProductsList()
     */
    public ArrayList<Product> getAllProducts() {
        return sqlController.getAllProductsList();
    }

    /**
     * Gets the ArrayList of products that the current user can purchase
     * @return an ArrayList with all products not owned by the user
     */
    public ArrayList<Product> getPurchasableProducts() {
        return sqlController.getPurchasableProducts(this.currentUser);
    }

    /**
     * Removes the specified product from the database and returns a string representing what occurred.
     * @param product The product to remove
     * @return A string representing the action that occurred.
     */
    public String removeProduct(Product product) {
        SQLController.RemoveProductResult removeProductResult = sqlController.removeProduct(product);
        switch (removeProductResult){
            case REMOVED:
                return "Product removed.";
            case NOWALLET:
                return "User has no wallet.";
            default:
                return "Action canceled.";
        }
    }

    /**
     * This function takes a username and amount and adds the WoCoins to the user's balance.
     * @param username the username to transfer WoCoins to
     * @param amount the amount of WoCoins to transfer
     * @return a string representing the output
     */
    public String transferWocoinsToUser(String username, int amount) {
        switch (sqlController.transferWocoin(username, amount)) {
            case SUCCESS:
                return "Transfer complete.";
            case NOUSER:
                return "No such user.";
            case NOWALLET:
                return "User has no wallet.";
            case NEGATIVEINPUT:
                return "Expected an integer value greater than or equal to 1.";
        }
        return "";
    }

    /**
     * Sends a message from the current user to the seller of the given product.
     * @param product the product the message is about
     * @param message the message that should be sent
     * @return a string describing the operation that occurred.
     */
    public String sendMessage(Product product, String message) {
        Message newMessage = new Message(this.currentUser, message, product);
        switch (sqlController.sendMessage(newMessage)){
            case SENT:
                return "Message sent.";
            case INVALIDSENDER:
            case INVALIDRECIPIENT:
                return "No such user.";
            case NOWALLET:
                return "User has no wallet.";
            default:
                return "Action canceled.";
        }
    }

    /**
     * This function retrieves the messages for the currently logged in user
     * @return an ArrayList of messages sent to the current user
     */
    public ArrayList<Message> getUserMessages() {
        return sqlController.getMessagesForUser(this.currentUser);
    }

    /**
     * This function deletes a given message. The message must be fully qualified.
     * @param message the message to be deleted
     * @return a String representing what happened to the message.
     */
    public String deleteMessage(Message message) {
        switch (sqlController.deleteMessage(message)){
            case DELETED:
                return "Message deleted.";
            default:
                return "Message not deleted.";
        }
    }


    /**
     * Takes a message about a product and sends a reply to the initial
     * @param originalMessage the message that was originally sent to the user
     * @param messageReply the reply the user is sending
     * @return a String representing what happened when sending the message.
     */
    public String replyToMessage(Message originalMessage, String messageReply) {
        Message newMessage = new Message(this.currentUser, originalMessage.getSenderUsername(), originalMessage.getProduct());

        switch (sqlController.sendMessage(newMessage)){
            case SENT:
                return "Message sent.";
            case INVALIDSENDER:
            case INVALIDRECIPIENT:
                return "No such user.";
            case NOWALLET:
                return "User has no wallet.";
            default:
                return "Message reply not sent.";
        }
    }

    public String getUserBalance() {
        BigInteger potentialBalance = sqlController.getUserBalance(this.currentUser);

        if (potentialBalance == null) {
            return "No such user.";
        }
        else if (potentialBalance.signum() < 0) {
            return "User has no wallet.";
        }
        else {
            String formatString = potentialBalance.equals(new BigInteger("1")) ? "User has %s WoCoin." : "User has %s WoCoins.";
            return String.format(formatString, potentialBalance.toString());
        }
    }
}
