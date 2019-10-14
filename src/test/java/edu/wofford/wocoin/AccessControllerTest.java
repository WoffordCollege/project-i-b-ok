package edu.wofford.wocoin;

import edu.wofford.wocoin.main.AccessController;
import edu.wofford.wocoin.main.UIController;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AccessControllerTest implements UIController {

    private AccessController ac;

    private AccessController.Result result;
    private AccessController.AccessOptions[] accessOptions;

    @Before
    public void setup(){
        ac = new AccessController(this);
    }


    @Test
    public void testAdminLogin(){
        ac.login("", "adminpwd");
        assertEquals(result, AccessController.Result.SUCCESS);
        assertArrayEquals(accessOptions, new AccessController.AccessOptions[]{AccessController.AccessOptions.ADDUSER});
    }

    @Override
    public void updateDisplay(AccessController.Result actionResult, AccessController.AccessOptions[] userOptions) {
        this.result = actionResult;
        this.accessOptions = userOptions;
    }
}
