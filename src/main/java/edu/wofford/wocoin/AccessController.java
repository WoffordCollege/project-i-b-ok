package edu.wofford.wocoin;

import java.util.ArrayList;

public class AccessController {
    public enum AccessOptions {ADDUSER, DELETEUSER}

    public enum Result {SUCCESS, INVALID_PASSWORD, WRONG_PASSWORD, INVALID_USERNAME, UNKNOWN_USERNAME, UNHANDLED}

    private UIController ui;
    private SQLController sqlController;

    private ArrayList<AccessOptions> uiOptions;

    public AccessController(UIController currentUI) {
        ui = currentUI;
        uiOptions = new ArrayList<AccessOptions>();
        sqlController = new SQLController();
    }

    public AccessController(UIController currentUI, String dbFilename) {
        ui = currentUI;
        uiOptions = new ArrayList<AccessOptions>();
        sqlController = new SQLController(dbFilename);
    }

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
            ui.updateDisplay(Result.SUCCESS, getUIOptions());
        }
        else {
            ui.updateDisplay(Result.WRONG_PASSWORD, getUIOptions());
        }
    }


    public void addUser(String username, String password){
        // SQLDB.addUser

        SQLController.sqlResult result = sqlController.insertUser(username, password);

        switch (result){
            case ADDED:
                ui.updateDisplay(Result.SUCCESS, getUIOptions(), new String[] {username});
                break;
            case DUPLICATE:
                ui.updateDisplay(Result.INVALID_USERNAME, getUIOptions(), new String[] {username});
                break;
            case NORECORD:
            case NOTREMOVED:
            case NOTADDED:
            case REMOVED:
                ui.updateDisplay(Result.UNHANDLED, getUIOptions());
                break;
        }

    }


    public AccessOptions[] getUIOptions() {
        AccessOptions[] unfilledArray = new AccessOptions[uiOptions.size()];
        return uiOptions.toArray(unfilledArray);
    }
}
