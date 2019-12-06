package edu.wofford.wocoin;

import java.io.File;
import java.io.IOException;

import java.io.FileWriter;

import org.apache.commons.io.FileUtils;

import org.json.simple.JSONObject;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.junit.*;

import static edu.wofford.wocoin.WalletUtilities.addWocoinToUser;
import static org.junit.Assert.*;
import java.sql.*;

public class WalletUtilitiesTest {

    private SQLController foobar2 = new SQLController();

    @AfterClass
    public static void destroy(){
        try{
            FileUtils.deleteDirectory(new File("test"));
        } catch(Exception e){
            System.out.println(e.toString());
        }
    }

    @Test
    public final void createWalletSUCCESSTest() {
        Pair<String, WalletUtilities.CreateWalletResult> val = WalletUtilities.createWallet("test", "Burdick", "");
        assertEquals(WalletUtilities.CreateWalletResult.SUCCESS, val.getSecond());
        assertTrue(val.getFirst().length() > 0);
    }

    @Test
    public final void createWalletALREADYEXISTSTest(){
        WalletUtilities.createWallet("test","Khan", "");
        File file = new File("test/Khan/mykeyfile.json");
        try {
            file.createNewFile();
        } catch (IOException e) { }

        Pair<String,WalletUtilities.CreateWalletResult> val = WalletUtilities.createWallet("test","Khan", "");
        assertEquals(0, val.getFirst().length());
        assertEquals(WalletUtilities.CreateWalletResult.FILEALREADYEXISTS, val.getSecond());
    }

    @Test
    public final void samepublicKeyTest() {
        File file = new File ("UTC--2019-08-14T05-39-33.567000000Z--a615316333ba8622fd5bb60fe39758b3515f774d.json");
        assertTrue(!WalletUtilities.walletInFilepathHasSamePublicKey(file, "a615316333ba8622fd5bb60fe39758b3515f774d", "jsmith"));
    }

    @Test
    public final void walletaddressTest() {
        File file = new File ("UTC--2019-08-14T05-39-33.567000000Z--a615316333ba8622fd5bb60fe39758b3515f774d.json");
        String address = WalletUtilities.getWalletAddressFromFile(file, "jsmith");
        assertEquals(null, address);
    }

    @Test
    public final void purchaseProductTest() {
        SQLController sql = new SQLController("test.db");
        Product newProduct = new Product("admin", 2, "toy", "a toy");
        SQLController.AddProductResult result = sql.addProduct(newProduct);
        BigInteger coinAmount = BigInteger.valueOf(2);
        WalletUtilities.addWocoinToUser("UTC--2019-08-14T05-39-33.567000000Z--a615316333ba8622fd5bb60fe39758b3515f774d.json", coinAmount);
        File filebuy = new File ("UTC--2019-08-14T05-39-33.567000000Z--a615316333ba8622fd5bb60fe39758b3515f774d.json");
        WalletUtilities.buyProduct(filebuy, "jsmith", newProduct, "UTC--2019-08-07T17-24-10.532680697Z--0fce4741f3f54fbffb97837b4ddaa8f769ba0f91.json");
    }
   }
