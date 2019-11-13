package edu.wofford.wocoin;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.security.*;
import java.sql.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.DefaultBlockParameterName;


public class Utilities {
	
	public static void createNewDatabase(String filename) {
        String url = "jdbc:sqlite:" + filename;
        String users = "CREATE TABLE IF NOT EXISTS users (" +
					   "id text PRIMARY KEY, " + 
					   "salt integer NOT NULL, " + 
					   "hash text NOT NULL)";
		String wallets = "CREATE TABLE IF NOT EXISTS wallets (" +
					 	 "id text PRIMARY KEY, " +
						 "publickey text NOT NULL, " +
						 "FOREIGN KEY (id) REFERENCES users(id) " +
						 "ON DELETE CASCADE, " +
						 "UNIQUE(publickey))";
		String products = "CREATE TABLE IF NOT EXISTS products (" +
						  "id integer PRIMARY KEY, " +
						  "seller text NOT NULL, " +
						  "price integer NOT NULL, " +
						  "name text NOT NULL, " + 
						  "description text NOT NULL, " +
						  "FOREIGN KEY (seller) REFERENCES wallets(publickey) " +
						  "ON DELETE CASCADE ON UPDATE CASCADE)";
		String messages = "CREATE TABLE IF NOT EXISTS messages (" +
						  "id integer PRIMARY KEY, " +
						  "sender text NOT NULL, " +
						  "recipient text NOT NULL, " +
						  "productid integer NOT NULL, " + 
						  "message text NOT NULL, " +
						  "dt datetime DEFAULT CURRENT_TIMESTAMP, " +
						  "FOREIGN KEY (sender) REFERENCES wallets(publickey), " +
						  "FOREIGN KEY (recipient) REFERENCES wallets(publickey), " +
						  "FOREIGN KEY (productid) REFERENCES products(id) " +
						  "ON DELETE CASCADE ON UPDATE CASCADE)";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(users);
            stmt.execute(wallets);
            stmt.execute(products);
            stmt.execute(messages);
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	public static void createTestDatabase(String filename) {
		createNewDatabase(filename);
        String url = "jdbc:sqlite:" + filename;
        String[] sqls = { "INSERT INTO users (id, salt, hash) VALUES (\"jdoe\", 13587, \"ebd3528832b124bb7886cd8e8d42871c99e06d5f3ad0c6ee883f6219b2b6a955\")",
        				  "INSERT INTO users (id, salt, hash) VALUES (\"jsmith\", 52196, \"9d3194cf601e62d35f144abebcea7704ad005402e102d134bd8f82ac469c2ec9\")",
        				  "INSERT INTO users (id, salt, hash) VALUES (\"hjones\", 47440, \"5d94ecaff496ac900a1f68ec950153aa1f500d06227b65167f460e5dd20a959b\")",
        				  "INSERT INTO users (id, salt, hash) VALUES (\"srogers\", 54419, \"26f2573d733da38fb3cd09eb79f884bbe63010570d394de7d8809b65823da85a\")",
        				  "INSERT INTO wallets (id, publickey) VALUES (\"jdoe\", \"587888ea2b080656816aad7e0bc8f1cf3cf0bced\")",
        				  "INSERT INTO wallets (id, publickey) VALUES (\"jsmith\", \"a615316333ba8622fd5bb60fe39758b3515f774d\")",
        				  "INSERT INTO wallets (id, publickey) VALUES (\"hjones\", \"e9d572572eaed1550f57bd41eec8105ad1f082b1\")",
        				  "INSERT INTO wallets (id, publickey) VALUES (\"srogers\", \"fab258997f9b8f33892e111515b21164205ae02a\")",
        				  "INSERT INTO products (seller, price, name, description) VALUES (\"a615316333ba8622fd5bb60fe39758b3515f774d\", 2, \"Zombieland\", \"DVD\")",
        				  "INSERT INTO products (seller, price, name, description) VALUES (\"587888ea2b080656816aad7e0bc8f1cf3cf0bced\", 3, \"apple\", \"small\")",
        				  "INSERT INTO products (seller, price, name, description) VALUES (\"a615316333ba8622fd5bb60fe39758b3515f774d\", 1, \"skittles\", \"a half-eaten bag\")",
        				  "INSERT INTO products (seller, price, name, description) VALUES (\"587888ea2b080656816aad7e0bc8f1cf3cf0bced\", 4, \"paper\", \"a ream for a printer\")",
        				  "INSERT INTO products (seller, price, name, description) VALUES (\"587888ea2b080656816aad7e0bc8f1cf3cf0bced\", 2, \"chalk\", \"taken from a classroom\")",
        				  "INSERT INTO products (seller, price, name, description) VALUES (\"587888ea2b080656816aad7e0bc8f1cf3cf0bced\", 4, \"trip to Charlotte\", \"no questions asked\")",
        				  "INSERT INTO products (seller, price, name, description) VALUES (\"a615316333ba8622fd5bb60fe39758b3515f774d\", 4, \"Risk\", \"board game\")",
        				  "INSERT INTO messages (sender, recipient, productid, message) VALUES (\"a615316333ba8622fd5bb60fe39758b3515f774d\", \"587888ea2b080656816aad7e0bc8f1cf3cf0bced\", 2, \"is it a red apple?\")",
        				  "INSERT INTO messages (sender, recipient, productid, message) VALUES (\"587888ea2b080656816aad7e0bc8f1cf3cf0bced\", \"a615316333ba8622fd5bb60fe39758b3515f774d\", 2, \"no it's green\")",
        				  };

        try (Connection conn = DriverManager.getConnection(url)) {
        	for (String sql : sqls) {
	            Statement stmt = conn.createStatement();
	            stmt.executeUpdate(sql);
	            stmt.close();
	            // Wait for one second so that timestamps are different.
	            try {
	            	Thread.sleep(1000);
	            } catch (InterruptedException e) {
	            	Thread.currentThread().interrupt();
	            }
        	}
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	public static String applySha256(String input) {		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static int generateSalt() {
	    return (int)(Math.random() * 99999);
	}

	public static BigInteger getBalance(String publicKey) {
		try {
			Web3j web3 = Web3j.build(new HttpService());
			EthGetBalance ethGetBalance = web3.ethGetBalance("0x" + publicKey, DefaultBlockParameterName.LATEST)
	        	                              .sendAsync()
	            	                          .get();
	    	return ethGetBalance.getBalance();
		} catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return BigInteger.valueOf(-1);
	}


}
