package edu.wofford.wocoin;

import java.io.File;
import java.io.IOException;

import java.io.FileWriter;

import org.codehaus.plexus.util.FileUtils;
import org.json.simple.JSONObject;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.junit.*;
import static org.junit.Assert.*;
import java.sql.*;

public class WalletUtilitiesTest {
//    @After
//    public final void tearDown() {
//        File index = new File("test");
//        try {
//            FileUtils.deleteDirectory(index);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    @Test
    public final void createWalletSUCCESSTest() {
        Pair<String, WalletUtilities.CreateWalletResult> val = WalletUtilities.createWallet("test", "Burdick", "");
        assertEquals(WalletUtilities.CreateWalletResult.SUCCESS, val.getSecond());
        assertTrue(val.getFirst().length() > 0);
    }

    @Test
    public final void createWalletALREADYEXISTSTest(){
        WalletUtilities.createWallet("test","Khan", "");
        Pair<String,WalletUtilities.CreateWalletResult> val = WalletUtilities.createWallet("test","Khan", "");
        assertEquals(0, val.getFirst().length());
        assertEquals(WalletUtilities.CreateWalletResult.FILEALREADYEXISTS, val.getSecond());
    }
   }
