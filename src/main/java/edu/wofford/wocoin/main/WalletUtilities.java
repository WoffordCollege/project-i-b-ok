package edu.wofford.wocoin.main;

import java.io.File;
import java.io.IOException;

import gherkin.lexer.Pa;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.web3j.crypto.ECKeyPair;
import javafx.util.Pair;
import org.web3j.crypto.Keys;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class WalletUtilities {
    public enum CreateWalletResult{SUCCESS, FILEALREADYEXISTS}
    public Pair createWallet (String path) {
        CreateWalletResult result = CreateWalletResult.SUCCESS;
        String fileLocation = path + "/WoCoinWalletFile.json";
        File tempFile = new File("c:/temp/temp.txt");
        if(tempFile.exists()) {
            return new Pair(null, CreateWalletResult.FILEALREADYEXISTS);
        }

        String privateKey = "";
        String publicKey = "";
        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            privateKey = ecKeyPair.getPrivateKey().toString(16);
            publicKey = ecKeyPair.getPublicKey().toString(16);
        } catch (InvalidAlgorithmParameterException |NoSuchAlgorithmException |
        NoSuchProviderException e) {
            System.out.println(e.toString());
        }
        JSONObject WocoinWallet = new JSONObject();
        WocoinWallet.put("Private Key", privateKey);
        WocoinWallet.put("Public Key", publicKey);


        try (FileWriter file = new FileWriter(fileLocation)) {
            file.write(WocoinWallet.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pair(publicKey, result);
    }




    public WalletUtilities(){

    }
}
