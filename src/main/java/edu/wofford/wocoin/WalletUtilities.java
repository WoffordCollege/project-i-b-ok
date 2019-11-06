package edu.wofford.wocoin;

import java.io.File;
import java.io.IOException;

import java.io.FileWriter;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.web3j.crypto.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class WalletUtilities {
    public enum CreateWalletResult{SUCCESS, FILEALREADYEXISTS, FAILED}

    /**
     * This function creates a public and private key for a user's wallet which will be stored in a JSON File. The function
     * returns a pair which is the public key and SUCCESS if the wallet was created, ALREADYEXSITS if the file is already exists,
     * and FAILED if a file cannot be created
     * @param path the path of the user directory where the JSON file will go
     * @param username the username for which the wallet will be created for
     * @return SUCCESS if the wallet was created, ALREADYEXSITS if the file is already exists, and FAILED if a file cannot be created
     */
    public static Pair<String, CreateWalletResult> createWallet (String path, String username, String password) {
        Path filePath = Paths.get(path, username);

        File file = filePath.toFile();

        if (file.exists()) {
            return new Pair<>("", CreateWalletResult.FILEALREADYEXISTS);
        }
        else {
            file.mkdirs();
        }

        String wPath = filePath.toString();

        String walletName = null;
        Credentials credentials = null;

        try {
            walletName = WalletUtils.generateNewWalletFile(password, new File(wPath));
            credentials = WalletUtils.loadCredentials(password, Paths.get(wPath, walletName).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new Pair<>("", CreateWalletResult.FAILED);
        }

        if (credentials.getAddress().length() >= 2){
            String accountAddress = credentials.getAddress().substring(2);
            return new Pair<>(accountAddress, CreateWalletResult.SUCCESS);
        }
        else {
            return new Pair<>("", CreateWalletResult.FAILED);
        }


    }
}
