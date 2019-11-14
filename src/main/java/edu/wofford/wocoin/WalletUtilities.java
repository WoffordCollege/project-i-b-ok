package edu.wofford.wocoin;

import java.io.File;

import org.web3j.crypto.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class WalletUtilities {
    public enum CreateWalletResult{SUCCESS, FILEALREADYEXISTS, FAILED}


    /**
     * This function creates a public and private key for a user's wallet which will be stored in a JSON File. The function
     * returns a pair which is the public key and SUCCESS if the wallet was created, ALREADYEXSITS if the file is already exists,
     * and FAILED if a file cannot be created
     * @param path the path of the user directory where the JSON file will go
     * @param username the username for which the wallet will be created for
     * @param password the password of the user owning the wallet
     * @return SUCCESS if the wallet was created, ALREADYEXSITS if the file is already exists, and FAILED if a file cannot be created
     */
    public static Pair<String, CreateWalletResult> createWallet (String path, String username, String password) {
        Path directoryPath = Paths.get(path, username);
        Path finalFilePath = Paths.get(path, username, "mykeyfile.json");

        File finalKeyFile = finalFilePath.toFile();

        if (finalKeyFile.exists()) {
            return new Pair<>("", CreateWalletResult.FILEALREADYEXISTS);
        }
        else {
            directoryPath.toFile().mkdirs();
        }

        String wPath = directoryPath.toString();

        String walletName;
        Credentials credentials;

        try {
            walletName = WalletUtils.generateNewWalletFile(password, directoryPath.toFile());
            File walletFile = Paths.get(path, username, walletName).toFile();
            credentials = WalletUtils.loadCredentials(password, walletFile);
            walletFile.renameTo(finalKeyFile);
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
