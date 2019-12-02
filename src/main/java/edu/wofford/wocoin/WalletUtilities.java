package edu.wofford.wocoin;

import java.io.File;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

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
    public enum PurchaseProductResult{SUCCESS, INSUFFICIENTFUNDS, NOWALLETFILE, FAILED}


    /**
     * This function creates a public and private key for a user's wallet which will be stored in a JSON File. The function
     * returns a pair which is the public key and SUCCESS if the wallet was created, FILEALREADYEXISTS if the file is already exists,
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

        String walletName;
        Credentials credentials;

        try {
            walletName = WalletUtils.generateNewWalletFile(password, directoryPath.toFile());
            File walletFile = Paths.get(path, username, walletName).toFile();
            credentials = WalletUtils.loadCredentials(password, walletFile);
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
     * This function adds the specified amount of WoCoins to the balance of the given wallet.
     * @param wallet the wallet to transfer WoCoins to
     * @param amt the amount of WoCoin to be transferred.
     */
    public static void addWocoinToUser(String wallet, BigInteger amt) {
        createWocoinTransaction(new File("ethereum/node0/keystore/UTC--2019-08-07T17-24-10.532680697Z--0fce4741f3f54fbffb97837b4ddaa8f769ba0f91.json"), "adminpwd", wallet, amt);
    }

    private static boolean createWocoinTransaction(File walletFile, String senderPassword, String recipientWallet, BigInteger amount) {
        Web3j web3 = Web3j.build(new HttpService());

        Credentials senderCredentials;
        try {
            senderCredentials = WalletUtils.loadCredentials(senderPassword, walletFile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        TransactionReceipt receipt = null;
        try {
            if (senderCredentials != null) {
                receipt = Transfer.sendFunds(web3, senderCredentials, "0x" + recipientWallet, new BigDecimal(amount), Convert.Unit.WEI).sendAsync().get();
            }
        } catch (Exception ignored) {return false;}

        return receipt != null && receipt.isStatusOK();
    }

    /**
     * This function takes a wallet file, password, and product to allow a user to buy a specified product with their ethereum balance.
     * Then checks if there is a valid wallet file in the given path with a subdirectory of username and returns {@link PurchaseProductResult#NOWALLETFILE} if not
     * Then checks if the user balance is more than the price of the product, if not, returns {@link PurchaseProductResult#INSUFFICIENTFUNDS}
     * If all criteria are met, it transfers the funds from the current user's wallet to the wallet of the seller of the product,
     * then updates the seller id of the product in the Wocoin database to the ID of the user who bought the product
     * @param walletFile the file of the directory holding the user's subdirectory
     * @param password the password of the user buying the product
     * @param boughtProduct the product the user wants to buy
     * @return a {@link PurchaseProductResult} indicating the operation that occurred.
     */
    public static PurchaseProductResult buyProduct(File walletFile, String password, Product boughtProduct, String sellerWallet) {
        if (!walletFile.exists()) {
            return PurchaseProductResult.NOWALLETFILE;
        } else {
            BigInteger userBalance = Utilities.getBalance(getWalletAddressFromFile(walletFile, password));
            BigInteger productPrice = BigInteger.valueOf(boughtProduct.getPrice());
            if (userBalance.compareTo(productPrice) < 0) {
                return PurchaseProductResult.INSUFFICIENTFUNDS;
            }
            else {
                if (createWocoinTransaction(walletFile, password, sellerWallet, BigInteger.valueOf(boughtProduct.getPrice()))) {
                    return PurchaseProductResult.SUCCESS;
                }
                else {
                    return PurchaseProductResult.FAILED;
                }
            }
        }

    }

    /**
     * This function checks that the given file exists and tests if the public key in the file is the same as the given public key.
     * @param walletFile the wallet file to retrieve credentials from
     * @param publicKey the public key to compare the credentials
     * @param userPassword the password of the user owning the wallet file.
     * @return true if the wallet file credentials match the given public key and false otherwise
     */
    public static boolean walletInFilepathHasSamePublicKey(File walletFile, String publicKey, String userPassword) {
        String fileWalletAddress = WalletUtilities.getWalletAddressFromFile(walletFile, userPassword);
        return fileWalletAddress != null && fileWalletAddress.equals(publicKey);
    }

    /**
     * This function returns the public key of the given wallet file if it is valid.
     * @param walletFile the wallet file to retrieve credentials from
     * @param userPassword the password of the user owning the wallet file.
     * @return a string containing the address of the wallet file
     */
    public static String getWalletAddressFromFile(File walletFile, String userPassword) {
        Credentials credentials;
        try {
            credentials = WalletUtils.loadCredentials(userPassword, walletFile);
        } catch (IOException | CipherException ignored) {
            return null;
        }

        return credentials != null ? credentials.getAddress().substring(2) : null;
    }

}
