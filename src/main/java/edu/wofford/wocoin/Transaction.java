package edu.wofford.wocoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class Transaction {
    private static final Logger log = LoggerFactory.getLogger(Transaction.class);

    public static void send() throws Exception {
        Web3j web3j = Web3j.build(new HttpService());
        log.info("Connected to Ethereum client version: "
                + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        Credentials credentials =
                WalletUtils.loadCredentials(
                        "adminpwd",
                        "C:\\Users\\cburd\\project-i-b-ok\\ethereum\\node0\\keystore\\UTC--2019-08-07T17-24-10.532680697Z--0fce4741f3f54fbffb97837b4ddaa8f769ba0f91.json");
        log.info("Credentials loaded");
        log.info("Sending Ether ..");
        TransactionReceipt transferReceipt = Transfer.sendFunds(
                web3j, credentials,
                "0xa615316333ba8622fd5bb60fe39758b3515f774d",
                BigDecimal.valueOf(1), Convert.Unit.ETHER).sendAsync()
                .get();
        log.info("Transaction complete : "
                + transferReceipt.getTransactionHash());
    }

    /*public static void send() throws Exception {
        Web3j web3j = Web3j.build(new HttpService());
        log.info("Connected to Ethereum client version: "
                + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        Credentials credentials =
                WalletUtils.loadCredentials(
                        "jsmith",
                        "C:\\Users\\cburd\\project-i-b-ok\\ethereum\\node0\\keystore\\UTC--2019-08-14T05-39-33.567000000Z--a615316333ba8622fd5bb60fe39758b3515f774d.json");
        log.info("Credentials loaded");
        log.info("Sending Ether ..");
        TransactionReceipt transferReceipt = Transfer.sendFunds(
                web3j, credentials,
                "0x0fce4741f3f54fbffb97837b4ddaa8f769ba0f91",
                BigDecimal.valueOf(.0997), Convert.Unit.ETHER).sendAsync()
                .get();
        log.info("Transaction complete : "
                + transferReceipt.getTransactionHash());
    }*/

    public static String getBalance(String publicKey) {
        try {
            Web3j web3 = Web3j.build(new HttpService());
            EthGetBalance ethGetBalance = web3.ethGetBalance("0x" + publicKey, DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
            BigInteger bal = ethGetBalance.getBalance();
            String actual = bal.toString();
            actual = actual.replace(actual.substring(actual.length() - 18), "");
            return actual;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }
}
