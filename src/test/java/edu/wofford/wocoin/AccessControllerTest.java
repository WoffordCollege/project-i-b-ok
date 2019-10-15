package edu.wofford.wocoin;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AccessControllerTest implements UIController {

    private AccessController ac;

    private AccessController.Result result;
    private AccessController.AccessOptions[] accessOptions;

    private String[] adminLogin = {"", "adminpwd"};

    @Before
    public void setup() {
        ac = new AccessController(this);
    }

    @Test
    public void testAdminLogin() {
        ac.login(adminLogin[0], adminLogin[1]);
        AccessController.AccessOptions[] compareArray = new AccessController.AccessOptions[1];
        compareArray[0] = AccessController.AccessOptions.ADDUSER;
        assertEquals(AccessController.Result.SUCCESS, result);
        assertArrayEquals(compareArray, accessOptions);
        ac.login("", "notadminpwd");
        compareArray = new AccessController.AccessOptions[0];
        assertEquals(AccessController.Result.WRONG_PASSWORD, result);
        assertArrayEquals(compareArray, accessOptions);
    }

    @Test
    public void testAddUser() {
        ac.login(adminLogin[0], adminLogin[1]);
        AccessController.AccessOptions[] compareArray = new AccessController.AccessOptions[1];
        compareArray[0] = AccessController.AccessOptions.ADDUSER;
        assertEquals(AccessController.Result.SUCCESS, result);
        assertArrayEquals(compareArray, accessOptions);

        ac.addUser("testuser", "testpassword");
        assertEquals(AccessController.Result.SUCCESS, result);
        assertArrayEquals(compareArray, accessOptions);

    }

    @Override
    public void updateDisplay(AccessController.Result actionResult, AccessController.AccessOptions[] userOptions) {
        this.result = actionResult;
        this.accessOptions = userOptions;
    }
}
