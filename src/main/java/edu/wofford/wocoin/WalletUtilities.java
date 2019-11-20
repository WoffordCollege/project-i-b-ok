package edu.wofford.wocoin;

import java.io.File;

import org.web3j.crypto.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class contains static functions for creating and interfacing with Wallet files.
 */
public class WalletUtilities {
    /**
     * An enumeration of the possible results of attempting to create a wallet file.
     */
    public enum CreateWalletResult{SUCCESS, FILEALREADYEXISTS, FAILED}

    /**
     * An enumeration of the possible results of attempting to purchase products.
     */
    public enum PurchaseProductResult{SUCCESS, INSUFFICIENTFUNDS, NOWALLET, NOWALLETFILE, WALLETKEYSDONTMATCH, FAILED}


    /**
     * This function creates a public and private key for a user's wallet which will be stored in a JSON File. The function
     * returns a pair which is the public key and SUCCESS if the wallet was created, ALREADYEXSITS if the file is already exists,
     * and FAILED if a file cannot be created
     * @param path the path of the user directory where the JSON file will go
     * @param username the username for which the wallet will be created for
     * @param password the password of the user owning the wallet
     * @return SUCCESS if the wallet was created, ALREADYEXSITS if the file is already exists, and FAILED if a file cannot be created
     */
    public static Pair<String, CreateWalletResult> createWallet(String path, String username, String password) {
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

    /**
     * This function takes a path, username, password, product, and sqlController to allow a user to buy a product with their ethereum balance.
     * If first checks if the user has a wallet in the database and returns {@link PurchaseProductResult#NOWALLET} if not
     * Then checks if there is a valid wallet file in the given path with a subdirectory of username and returns {@link PurchaseProductResult#NOWALLETFILE} if not
     * Then checks if the wallet file address and the wallet address in the database are the same, if not, returns {@link PurchaseProductResult#WALLETKEYSDONTMATCH}
     * Then checks if the user balance is more than the price of the product, if not, returns {@link PurchaseProductResult#INSUFFICIENTFUNDS}
     * If all criteria are met, it transfers the funds from the current user's wallet to the wallet of the seller of the product,
     * then updates the seller id of the product in the Wocoin database to the ID of the user who bought the product
     * @param path the path of the directory holding the user's subdirectory
     * @param username the username of the user buying the product
     * @param password the password of the user buying the product
     * @param boughtProduct the product the user wants to buy
     * @param sqlController the {@link SQLController} of the current Wocoin database
     * @return a {@link PurchaseProductResult} indicating the operation that occurred.
     */
    public static PurchaseProductResult buyProduct(String path, String username, String password, Product boughtProduct, SQLController sqlController){


        return PurchaseProductResult.FAILED;
    }


}
