package edu.wofford.wocoin;

/**
 * This class stores the helper methods that can be used to create a Console style GUI
 * It stores a SQLController which is used to perform the necessary operations on the database
 */
public class ConsoleController {

    private SQLController sqlController;

    public enum UIState {EXIT, LOGIN, USER, ADMINISTRATOR}

    private UIState currentState;

    private String currentUser;

    /**
     * Constructs a new ConsoleController with the given {@link SQLController}. Sets the current state of the UI to Login
     * @param sqlController The {@link SQLController} with a given WocoinDatabase that is modified with the Controller
     */
    public ConsoleController(SQLController sqlController) {
        this.currentState = UIState.LOGIN;
        this.sqlController = sqlController;
        this.currentUser = null;
    }


    /**
     * @return returns the String representation of the current screen.
     */
    public String getCurrentUIString() {
        switch (currentState) {
            case EXIT:
                return "";
            case LOGIN:
                return "Please select from the following options:\n1: exit\n2: administrator\n3: user";
            case USER:
                return "Please select from the following options:\n1: back\n2: create wallet";
            case ADMINISTRATOR:
                return "Please select from the following options:\n1: back\n2: add user\n3: remove user";
        }
        return null;
    }

    /**
     * This function updates the state of the display to Administrator if the input password is correct.
     * returns true if the UIState is already administrator, or if the password is correct
     * @param password this is the password the user uses to attempt to login as administrator
     * @return true if the UIState is already administrator or if the password is correct, and returns false otherwise
     */
    public boolean adminLogin(String password) {
        if (currentState == UIState.ADMINISTRATOR) {
            return true;
        }
        else if (password.equals("adminpwd")) {
            currentState = UIState.ADMINISTRATOR;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * This function updates the state of the display to User if the username and password combination is correct.
     * If the login is successful, we store the username of the current user for wallet interaction
     * returns true if the {@link UIState} is already User, or if the username and password combination is correct.
     * returns false otherwise
     * @param username the username of the user logging in
     * @param password the password of the user logging in
     * @return true if the {@link UIState} is already User, or if the username and password combination is correct and false otherwise.
     */
    public boolean userLogin(String username, String password) {
        if (currentState == UIState.USER) {
            return true;
        }
        else if (sqlController.userLogin(username, password) == SQLController.LoginResult.SUCCESS) {
                currentState = UIState.USER;
                this.currentUser = username;
                return true;
        }
        else{
            return false;
        }
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
     * @return a {@link WalletUtilities.CreateWalletResult} relating to the result of the executed operation (see {@link WalletUtilities}).
     */
    public WalletUtilities.CreateWalletResult addWalletToUser(String filepath) {
        if (currentUser == null || currentUser.length() == 0) {
            return WalletUtilities.CreateWalletResult.FAILED;
        }
        Pair<String, WalletUtilities.CreateWalletResult> keyAndResult = WalletUtilities.createWallet(filepath, currentUser);
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
        currentState = UIState.LOGIN;
    }

    /**
     * Sets the current state of the UI to exit
     */
    public void exit() {
        currentState = UIState.EXIT;
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
        if (currentState == UIState.ADMINISTRATOR) {
            SQLController.AddUserResult result = sqlController.insertUser(username, password);

            switch (result) {
                case ADDED:
                    return username + " was added.";
                case DUPLICATE:
                    return username + " already exists.";
                case NOTADDED:
                    return username + " was not added.";
            }
        }

        return null;
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
        if (currentState == UIState.ADMINISTRATOR) {
            SQLController.RemoveUserResult result = sqlController.removeUser(username);

            switch (result) {
                case REMOVED:
                    return username + " was removed.";
                case NORECORD:
                    return username + " does not exist.";
                case NOTREMOVED:
                    return username + " was not removed.";
            }
        }

        return null;
    }

    /**
     * This function can be used to determine what display type is currently
     * @return returns a {@link UIState} reflecting the state of the UI
     */
    public UIState getCurrentState() {
        return currentState;
    }

    /**
     * This can be used to determine who is the currently logged in user
     * @return the username of the user that is currently logged in, null if no one is logged in
     */
    public String getCurrentUser() {
        return currentUser;
    }
}
