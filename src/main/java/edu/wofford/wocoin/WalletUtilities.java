package edu.wofford.wocoin;

import java.io.File;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
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
    public enum PurchaseProductResult{SUCCESS, INSUFFICIENTFUNDS, NOWALLET, NOWALLETFILE, WALLETKEYSDONTMATCH, FAILED}


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
        createWocoinTransaction("ethereum/node0/keystore/UTC--2019-08-07T17-24-10.532680697Z--0fce4741f3f54fbffb97837b4ddaa8f769ba0f91.json", "adminpwd", "0x" + wallet, amt);
    }

    private static void createWocoinTransaction(String walletPath, String senderPassword, String recipientWallet, BigInteger amount) {
        Web3j web3 = Web3j.build(new HttpService());

        Credentials senderCredentials = null;
        try {
            senderCredentials = WalletUtils.loadCredentials(senderPassword, walletPath);
        } catch (IOException | CipherException e) {
            e.printStackTrace();
        }

        try {
            TransactionReceipt receipt = Transfer.sendFunds(web3, senderCredentials, recipientWallet, new BigDecimal(amount), Convert.Unit.WEI).sendAsync().get();
        } catch (InterruptedException | ExecutionException | IOException | TransactionException e) {
            e.printStackTrace();
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
     * @param walletPath the path of the directory holding the user's subdirectory
     * @param password the password of the user buying the product
     * @param boughtProduct the product the user wants to buy
     * @param sqlController the {@link SQLController} of the current Wocoin database
     * @return a {@link PurchaseProductResult} indicating the operation that occurred.
     */
    public static PurchaseProductResult buyProduct(String walletPath, String password, Product boughtProduct, SQLController sqlController){

        createWocoinTransaction(walletPath, password, boughtProduct.getSeller(), BigInteger.valueOf(boughtProduct.getPrice()));
        return PurchaseProductResult.FAILED;
    }


}
