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

    @Ignore
    @Test
    public final void successfulProductAdd(){
        foo.insertUser("john","Wofford1854");
        foo.addWallet("john","j12345");
        assertEquals(SQLController.AddProductResult.ADDED,foo.addProduct("john","x","This is the description.", 20));
        //assertEquals("j12345",);
    }

    @Ignore
    @Test
    public final void ProductAddWithoutWallet(){

    }

    @Ignore
    @Test
    public final void ProductAddNoName(){

    }

    @Ignore
    @Test
    public final void ProductAddNoDescription(){

    }

    @Ignore
    @Test
    public final void ProductAddNegativePrice(){

    }

    @Ignore
    @Test
    public final void ProductAddZeroPrice(){

    }

    @Ignore
    @Test
    public final void ProductAddNoUser(){

    }
}
