package edu.wofford.wocoin;

import java.util.ArrayList;

public class AccessController {

    public enum AccessOptions {ADDUSER, DELETEUSER}

    public enum Result {SUCCESS, INVALID_PASSWORD, WRONG_PASSWORD, INVALID_USERNAME, UNKNOWN_USERNAME, UNHANDLED}

    private UIController ui;
    private SQLController sqlController;

    private ArrayList<AccessOptions> uiOptions;

    /**
     * Constructor
     * @param currentUI an instance of an UI object
     */
    public AccessController(UIController currentUI) {
        ui = currentUI;
        uiOptions = new ArrayList<AccessOptions>();
        sqlController = new SQLController();
    }

    /**
     * Constructor
     * @param currentUI an instance of an UI object
     * @param dbFilename the database to access
     */
    public AccessController(UIController currentUI, String dbFilename) {
        ui = currentUI;
        uiOptions = new ArrayList<AccessOptions>();
        sqlController = new SQLController(dbFilename);
    }

    /**
     *
     * @param username the name of the user
     * @param password the associated password
     */
    public void login(String username, String password){
        uiOptions.clear();

        /* Stub for logging into sqldb
        Result loginResult = sqlController.userLogin(username, password);
        switch (loginResult) {
            case SUCCESS:
                uiOptions.add(AccessOptions.ADDUSER);
        }
         */

        if (password.equals("adminpwd")){
            uiOptions.add(AccessOptions.ADDUSER);
            uiOptions.add(AccessOptions.DELETEUSER);
            ui.updateDisplay(Result.SUCCESS, getUIOptions());
        }
        else {
            ui.updateDisplay(Result.WRONG_PASSWORD, getUIOptions());
        }
    }

    /**
     *
     * @param username the name of the user to add
     * @param password the associated password for the user
     */
    public void addUser(String username, String password){
        // SQLDB.addUser

        SQLController.sqlResult result = sqlController.insertUser(username, password);

        switch (result){
            case ADDED:
                ui.updateDisplay(Result.SUCCESS, getUIOptions(), new String[] {"add", username});
                break;
            case DUPLICATE:
                ui.updateDisplay(Result.INVALID_USERNAME, getUIOptions(), new String[] {"add", username});
                break;
            case NORECORD:
            case NOTREMOVED:
            case NOTADDED:
            case REMOVED:
                ui.updateDisplay(Result.UNHANDLED, getUIOptions());
                break;
        }

    }

    /**
     *
     * @param username the name of the user to be removed
     */
    public void removeUser(String username) {
        SQLController.sqlResult result = sqlController.removeUser(username);

        switch (result){

            case ADDED:
                break;
            case NOTADDED:
                break;
            case DUPLICATE:
                break;
            case REMOVED:
                ui.updateDisplay(Result.SUCCESS, getUIOptions(), new String[] {"remove", username});
                break;
            case NOTREMOVED:
                break;
            case NORECORD:
                ui.updateDisplay(Result.INVALID_USERNAME, getUIOptions(), new String[] {"remove", username});
                break;
        }
    }

    /**
     *
     * @return 
     */
    public AccessOptions[] getUIOptions() {
        AccessOptions[] unfilledArray = new AccessOptions[uiOptions.size()];
        return uiOptions.toArray(unfilledArray);
    }
}
