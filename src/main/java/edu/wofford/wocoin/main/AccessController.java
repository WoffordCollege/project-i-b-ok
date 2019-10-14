package edu.wofford.wocoin.main;

import java.util.ArrayList;

public class AccessController {
    public enum AccessOptions {ADDUSER, DELETEUSER}

    public enum Result {SUCCESS, INVALID_PASSWORD, WRONG_PASSWORD, INVALID_USERNAME, UNKNOWN_USERNAME}

    private UIController ui;
    private SQLController sqlController;

    private ArrayList<AccessOptions> uiOptions;

    public AccessController(UIController currentUI) {
        ui = currentUI;
        uiOptions = new ArrayList<AccessOptions>();
        sqlController = new SQLController();
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
            ui.updateDisplay(Result.INVALID_PASSWORD, getUIOptions());
        }
    }


    public void addUser(String username, String password){
        // SQLDB.addUser

        uiOptions.add(AccessOptions.ADDUSER);
        ui.updateDisplay(Result.SUCCESS, getUIOptions());
    }


    public AccessOptions[] getUIOptions() {
        return (AccessOptions[]) uiOptions.toArray();
    }
}
