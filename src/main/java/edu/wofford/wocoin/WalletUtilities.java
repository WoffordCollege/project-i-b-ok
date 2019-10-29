package edu.wofford.wocoin;

import java.io.File;
import java.io.IOException;

import java.io.FileWriter;

import org.json.simple.JSONObject;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class WalletUtilities {
    public enum CreateWalletResult{SUCCESS, FILEALREADYEXISTS, FAILED}

    public static Pair<String, CreateWalletResult> createWallet (String path, String username) {
        CreateWalletResult result = CreateWalletResult.SUCCESS;

        Path filePath = Paths.get(System.getProperty("user.dir"), path, username, "mykeyfile.json");

        File file = filePath.toFile();

        if(file.exists()) {
            return new Pair<>(null, CreateWalletResult.FILEALREADYEXISTS);
        }
        else {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                return new Pair<>("", CreateWalletResult.FAILED);
            }
        }

        String privateKey = null;
        String publicKey = null;
        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            privateKey = ecKeyPair.getPrivateKey().toString(16);
            publicKey = ecKeyPair.getPublicKey().toString(16);
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            System.out.println(e.toString());
            return new Pair<>("", CreateWalletResult.FAILED);
        }

        JSONObject wocoinWallet = new JSONObject();
        wocoinWallet.put("Private Key", privateKey);
        wocoinWallet.put("Public Key", publicKey);

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(wocoinWallet.toJSONString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Pair<>(publicKey, result);
    }
}
