package edu.wofford.wocoin;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.sql.*;

public class SQLControllerTest {

    private SQLController foo;

    @Before
    public void setup(){
        foo = new SQLController();
    }

    @BeforeClass
    public static void setupDB(){
        new File("wocoinDatabase.sqlite3").delete();
        Utilities.createTestDatabase("wocoinDatabase.sqlite3");
    }

    /*@AfterClass
    public static void destroyDB(){
        new File("wocoinDatabase.sqlite3").delete();
    }*/

    @Test
    public final void testConstructor(){
        SQLController bar = new SQLController("testDB.sqlite3");
        assertEquals("jdbc:sqlite:testDB.sqlite3", bar.getPath());

        assertEquals("jdbc:sqlite:wocoinDatabase.sqlite3", foo.getPath());
    }


    @Test
    public final void walletExists(){
        assertTrue(foo.findWallet("srogers"));
    }

    @Test
    public final void walletNotExists(){
        assertTrue(!foo.findWallet("tstark"));
    }

    @Test
    public final void addWallet(){
        foo.removeWallet("test");
        assertEquals(SQLController.AddWalletResult.ADDED, foo.addWallet("test","8675309"));
    }

    @Test
    public final void addWalletDuplicate(){
        foo.addWallet("test","8675309");
        assertEquals(SQLController.AddWalletResult.ALREADYEXISTS, foo.addWallet("test","8675309"));
    }

    @Test
    public final void replaceWallet(){
        foo.addWallet("test","8675309");
        assertEquals(SQLController.ReplaceWalletResult.REPLACED, foo.replaceWallet("test","867530"));
        assertEquals("867530",foo.RetrievePublicKey("test"));
    }

    @Test
    public final void replaceNonExistentWallet(){
        assertEquals(SQLController.ReplaceWalletResult.NOSUCHWALLET, foo.replaceWallet("tstark","86753099"));
    }

    @Test
    public final void removeWallet(){
        foo.addWallet("bbanner","q8675309");
        assertEquals(SQLController.RemoveWalletResult.REMOVED,foo.removeWallet("bbanner"));
    }

    @Test
    public final void removeNonExistentWallet(){
        foo.removeWallet("bbanner");
        assertEquals(SQLController.RemoveWalletResult.NOSUCHWALLET,foo.removeWallet("bbanner"));
    }

    @Test
    public final void getPublicKeyTest(){
        foo.removeWallet("nfury");
        foo.addWallet("nfury","nf675309");
        assertEquals("nf675309",foo.RetrievePublicKey("nfury"));
    }

    @Test
    public final void publicKeyDoesNotExist(){
        foo.removeWallet("nfury");
        assertEquals("",foo.RetrievePublicKey("nfury"));
    }

    @Test
    public final void getName(){
        assertEquals("jdoe", foo.getName("587888ea2b080656816aad7e0bc8f1cf3cf0bced"));
    }

    @Test
    public final void getNameInvalidPublicKey(){
        assertEquals("", foo.getName("test"));
    }

    @Test
    public final void successfulProductAdd(){
        foo.insertUser("john","Wofford1854");
        foo.addWallet("john","j12345");
        assertEquals(SQLController.AddProductResult.ADDED,foo.addProduct("john","x","This is the description.", 20));
        try (Connection dataConn = DriverManager.getConnection(foo.getPath())) {
            PreparedStatement stSelect = dataConn.prepareStatement("SELECT * FROM products order by id desc limit 1");
            ResultSet dtr = stSelect.executeQuery();
            assertEquals("j12345", dtr.getString(2));
            assertEquals(20, dtr.getInt(3));
            assertEquals("x", dtr.getString(4));
            assertEquals("This is the description.", dtr.getString(5));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Test
    public final void ProductAddWithoutWallet(){
        foo.insertUser("newUser","password");
        assertEquals(SQLController.AddProductResult.NOWALLET,foo.addProduct("newUser","x","This is the description.", 20));
    }

    @Test
    public final void ProductAddNoUser(){
        assertEquals(SQLController.AddProductResult.NOWALLET,foo.addProduct("noName","x","This is the description.", 20));
    }

    @Test
    public final void ProductAddNoDescription(){
        assertEquals(SQLController.AddProductResult.EMPTYDESCRIPTION,foo.addProduct("jsmith","x","", 20));
    }

    @Test
    public final void ProductAddNegativePrice(){
        assertEquals(SQLController.AddProductResult.NONPOSITIVEPRICE,foo.addProduct("jsmith","x","This is the description.", -2));
    }

    @Test
    public final void ProductAddZeroPrice(){
        assertEquals(SQLController.AddProductResult.NONPOSITIVEPRICE,foo.addProduct("jsmith","x","This is the description.", 0));
    }

    @Test
    public final void ProductAddNoName(){
        assertEquals(SQLController.AddProductResult.EMPTYNAME,foo.addProduct("jsmith","","This is the description.", 20));
    }
}
