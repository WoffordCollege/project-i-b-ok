package gradle.cucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;
import cucumber.api.java.Before;
import cucumber.api.java.After;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.math.BigInteger;
import edu.wofford.wocoin.main.Main;
import edu.wofford.wocoin.Utilities;


public class FeaturesSteps {
    private int featureNumber;
    private String dbname;
    private String programInput;
    private String actualOutput;
    private String keyroot;
    private boolean alreadyRun;


    private void deleteFile(String filename) {
        if (filename != null && filename.length() > 0) {
            File f = new File(filename);
            if (f != null && f.exists()) {
                f.delete();
            }
        }
    }

    private void deleteDirectory(String directory) {
        if (directory != null && directory.length() > 0) {
            File dir = new File(directory);
            File[] allContents = dir.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    deleteDirectory(file.toString());
                }
            }
            dir.delete();
        }
    }

    
    @Before
    public void initialize() {
        featureNumber = 1;
        dbname = "";
        programInput = "";
        actualOutput = "";
        keyroot = "";
        alreadyRun = false;
    }

    @After
    public void teardown() {
        deleteFile(dbname);
        deleteDirectory(keyroot);
    }

    @Given("the feature is {int}")
    public void theFeatureIs(int feature) {
        featureNumber = feature;
    }

    @Given("the database is empty")
    public void theDatabaseIsEmpty() {
        dbname = "empty.db";
        deleteFile(dbname);
    }
    
    @Given("the database contains the user {string} with password {string}")
    public void theDatabaseContainsTheUserWithPassword(String user, String pass) {
        dbname = "populated.db";
        File f = new File(dbname);
        if (!f.exists()) {
            Utilities.createNewDatabase(dbname);
        }
        String url = "jdbc:sqlite:" + dbname;
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "INSERT INTO users (id, salt, hash) VALUES (?, ?, ?)";
            int salt = Utilities.generateSalt();
            String hash = Utilities.applySha256(pass + String.valueOf(salt));
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            pstmt.setInt(2, salt);
            pstmt.setString(3, hash);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Given("the database contains the wallet {string} with public key {string}")
    public void theDatabaseContainsTheWalletWithPublicKey(String user, String pubkey) {
        dbname = "populated.db";
        File f = new File(dbname);
        if (!f.exists()) {
            Utilities.createNewDatabase(dbname);
        }
        String url = "jdbc:sqlite:" + dbname;
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "INSERT INTO wallets (id, publickey) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            pstmt.setString(2, pubkey);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Given("the database contains a product {string} with description {string} and price {int} added by {string}")
    public void theDatabaseContainsAProductWithDescriptionAndPriceAddedBy(String name, String description, Integer price, String pubkey) {
        dbname = "populated.db";
        File f = new File(dbname);
        if (!f.exists()) {
            Utilities.createNewDatabase(dbname);
        }
        String url = "jdbc:sqlite:" + dbname;
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "INSERT INTO products (name, description, price, seller) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setInt(3, price);
            pstmt.setString(4, pubkey);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Given("the database contains a message {string} sent from {string} to {string} about product {int} on {string}")
    public void theDatabaseContainsAMessageSentFromToAboutProductOn(String message, String sender, String recipient, Integer productId, String datetime) {
        dbname = "populated.db";
        File f = new File(dbname);
        if (!f.exists()) {
            Utilities.createNewDatabase(dbname);
        }
        String url = "jdbc:sqlite:" + dbname;
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "INSERT INTO messages (sender, recipient, productid, message, dt) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sender);
            pstmt.setString(2, recipient);
            pstmt.setInt(3, productId);
            pstmt.setString(4, message);
            pstmt.setString(5, datetime);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    @Given("the directory {string} exists")
    public void theDirectoryExists(String path) {
        File keydir = new File(path);
        if (!keydir.isDirectory()) {
            keydir.mkdir();
        }
    }
    
    @Given("the directory {string} is empty")
    public void theDirectoryIsEmpty(String path) {
        File keydir = new File(path);
        for (File file: keydir.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }
    
    @Given("the directory {string} contains the file {string}")
    public void theDirectoryContainsTheFile(String path, String name) {
        File file = new File(path + File.separator + name);
        try {
            file.createNewFile();
        } catch (IOException e) { }
    }
    
    @Given("I attempt to perform as administrator with password {string}")
    public void iAttemptToPerformAsAdministratorWithPassword(String adminPassword) {
        programInput += "2\n" + adminPassword + "\n";
    }

    @Given("I attempt to perform as a user with username {string} and password {string}")
    public void iAttemptToPerformAsAUserWithUsernameAndPassword(String username, String password) {
        programInput += "3\n" + username + "\n" + password + "\n";;
    }

    @Given("the user {string} has {int} WoCoins using administrator password {string}")
    public void theUserHasWoCoinsUsingAdministratorPassword(String username, Integer amount, String password) {
        programInput += "2\n" + password + "\n4\n" + username + "\n" + amount + "\n1\n"; 
    }

    @When("I add the user {string} with password {string}")
    public void iAddTheUserWithPassword(String user, String pass) {
        programInput += "2\n" + user + "\n" + pass + "\n";
    }

    @When("I remove the user {string}")
    public void iRemoveTheUser(String user) {
        programInput += "3\n" + user + "\n";
    }

    @When("I create a wallet")
    public void iCreateAWallet() {
        programInput += "2\n";
    }
    
    @When("I use the directory {string}")
    public void iUseTheDirectory(String dir) {
        keyroot = dir;
        programInput += dir + "\n";
    }
    
    @When("I accept the default option")
    public void iAcceptTheDefaultOption() {
        programInput += "\n";
    }

    @When("I add a product named {string} with description {string} and price {int}")
    public void iAddAProductNamedWithDescriptionAndPrice(String name, String description, Integer price) {
        programInput += "3\n" + name + "\n";
        if (name.length() == 0) {
            programInput += "whatever\n";
        }
        programInput += description + "\n";
        if (description.length() == 0) {
            programInput += "whatever\n";
        }
        programInput += price + "\n";
        if (price < 1) {
            programInput += "42\n";
        }
    }

    @When("I remove product {int}")
    public void iRemoveProduct(Integer num) {
        programInput += "4\n" + num + "\n";
    }
    
    @When("I remove nonexistent product {int}")
    public void iRemoveNonexistentProduct(Integer num) {
        programInput += "4\n" + num + "\n1\n";
    }
    
    @When("I display products")
    public void iDisplayProducts() {
        programInput += "5\n";
    }

    @When("I check my messages")
    public void iCheckMyMessages() {
        programInput += "7\n";
    }

    @When("I send a message")
    public void iSendAMessage() {
        programInput += "6\n";
    }

    @When("I choose to cancel")
    public void iChooseToCancel() {
        programInput += "1\n";
    }
    
    @When("I choose product {int}")
    public void iChooseProduct(Integer productNum) {
        programInput += productNum + "\n";
    }
    
    @When("I provide the message {string}")
    public void iProvideTheMessage(String message) {
        programInput += message + "\n";
    }
    
    @When("I choose to reply to message {int}")
    public void iChooseToReplyToMessage(Integer messageNum) {
        programInput += messageNum + "\n2\n";
    }

    @When("I choose to delete message {int}")
    public void iChooseToDeleteMessage(Integer messageNum) {
        programInput += messageNum + "\n3\n";
    }

    @When("I transfer {int} WoCoins to {string}")
    public void iTransferWoCoinsTo(Integer amount, String receiver) {
        programInput += "4\n" + receiver + "\n" + amount + "\n";
        if (amount < 1) {
            programInput += "1\n";
        }
    }

    @When("I check my balance")
    public void iCheckMyBalance() {
        programInput += "8\n";
    }

    @When("I purchase a product")
    public void iPurchaseAProduct() {
        programInput += "9\n";
    }

    @When("I use the wallet home directory {string}")
    public void iUseTheWalletHomeDirectory(String walletHome) {
        programInput += walletHome + "\n";
    }

    @Then("I should be told {string}")
    public void iShouldBeTold(String expectedOutput) {
        if (!alreadyRun) {
            programInput += "1\n1\n1\n1\n";
            InputStream originalIn = System.in;
            PrintStream originalOut = System.out;
            try {
                System.setIn(new ByteArrayInputStream(programInput.getBytes()));
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                System.setOut(new PrintStream(outContent));
                String[] args = {Integer.toString(featureNumber), dbname};
                Main.main(args);
                actualOutput = outContent.toString();
            } finally {
                System.setIn(originalIn);
                System.setOut(originalOut);
            }
            alreadyRun = true;
        }
        assertThat(actualOutput, containsString(expectedOutput));
    }
    
    @Then("I should not be told {string}")
    public void iShouldNotBeTold(String expectedOutput) {
        if (!alreadyRun) {
            programInput += "1\n1\n1\n1\n";
            InputStream originalIn = System.in;
            PrintStream originalOut = System.out;
            try {
                System.setIn(new ByteArrayInputStream(programInput.getBytes()));
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                System.setOut(new PrintStream(outContent));
                String[] args = {Integer.toString(featureNumber), dbname};
                Main.main(args);
                actualOutput = outContent.toString();
            } finally {
                System.setIn(originalIn);
                System.setOut(originalOut);
            }
            alreadyRun = true;
        }
        assertThat(actualOutput, not(containsString(expectedOutput)));
    }
    
    @Then("the database should contain the user {string} with password {string}")
    public void theDatabaseShouldContainTheUserWithPassword(String user, String pass) {
        String url = "jdbc:sqlite:" + dbname;
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT id, salt, hash FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();
            assertThat(rs.next(), is(true));
            int salt = rs.getInt("salt");
            String actualHash = rs.getString("hash");
            String expectedHash = Utilities.applySha256(pass + String.valueOf(salt));
            assertThat(actualHash, is(expectedHash));
        } catch (SQLException e) {
            assertThat(e.toString(), true, is(false));
        }
    }

    @Then("the database should not contain the user {string}")
    public void theDatabaseShouldNotContainTheUser(String user) {
        String url = "jdbc:sqlite:" + dbname;
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT id, salt, hash FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();
            assertThat(rs.next(), is(false));
        } catch (SQLException e) {
            assertThat(e.toString(), true, is(false));
        }
    }

    @Then("the database should contain a wallet with username {string} and a nonempty public key")
    public void theDatabaseShouldContainAWalletWithUsernameAndANonemptyPublicKey(String username) {
        String url = "jdbc:sqlite:" + dbname;
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT publickey FROM wallets WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            assertThat(rs.next(), is(true));
            String pk = rs.getString("publickey");
            assertThat(pk.length(), greaterThan(0));
        } catch (SQLException e) {
            assertThat(e.toString(), true, is(false));
        }
    }

    @Then("the database should contain the product {string} with description {string} and price {int} added by {string} {int} times")
    public void theDatabaseShouldContainTheProductWithDescriptionAndPriceAddedByTimes(String name, String description, Integer price, String pubkey, int occurrences) {
        String url = "jdbc:sqlite:" + dbname;
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT COUNT(*) FROM products WHERE name = ? AND description = ? AND price = ? AND seller = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setInt(3, price);
            pstmt.setString(4, pubkey);
            ResultSet rs = pstmt.executeQuery();
            assertThat(rs.next(), is(true));
            int num = rs.getInt(1);
            assertThat(num, is(occurrences));
        } catch (SQLException e) {
            assertThat(e.toString(), true, is(false));
        }
    }

    @Then("the database should contain the message {string} sent from {string} to {string} about product {int} on {string}")
    public void theDatabaseShouldContainTheMessageSentFromToAboutProductOn(String message, String sender, String recipient, Integer productId, String datetime) {
        String url = "jdbc:sqlite:" + dbname;
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT COUNT(*) FROM messages WHERE sender = ? AND recipient = ? AND message = ? AND productid = ? ";
            if (datetime.equals("now")) {
                sql += "AND dt >= ? AND dt <= ?";
            } else {
                sql += "AND dt = ?";
            }
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sender);
            pstmt.setString(2, recipient);
            pstmt.setString(3, message);
            pstmt.setInt(4, productId);
            if (datetime.equals("now")) {
                Instant now = Instant.now();
                Date early = Date.from(now.minusSeconds(3));
                Date late = Date.from(now.plusSeconds(3));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                pstmt.setString(5, formatter.format(early));
                pstmt.setString(6, formatter.format(late));
            } else {
                pstmt.setString(5, datetime);
            }
            ResultSet rs = pstmt.executeQuery();
            assertThat(rs.next(), is(true));
            int num = rs.getInt(1);
            assertThat(num, is(1));
        } catch (SQLException e) {
            assertThat(e.toString(), true, is(false));
        }
    }

    @Then("the database should not contain the message {string} sent from {string} to {string} about product {int} on {string}")
    public void theDatabaseShouldNotContainTheMessageSentFromToAboutProductOn(String message, String sender, String recipient, Integer productId, String datetime) {
        String url = "jdbc:sqlite:" + dbname;
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT COUNT(*) FROM messages WHERE sender = ? AND recipient = ? AND message = ? AND productid = ? ";
            if (datetime.equals("now")) {
                sql += "AND dt >= ? AND dt <= ?";
            } else {
                sql += "AND dt = ?";
            }
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sender);
            pstmt.setString(2, recipient);
            pstmt.setString(3, message);
            pstmt.setInt(4, productId);
            if (datetime.equals("now")) {
                Instant now = Instant.now();
                Date early = Date.from(now.minusSeconds(3));
                Date late = Date.from(now.plusSeconds(3));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                pstmt.setString(5, formatter.format(early));
                pstmt.setString(6, formatter.format(late));
            } else {
                pstmt.setString(5, datetime);
            }
            ResultSet rs = pstmt.executeQuery();
            assertThat(rs.next(), is(true));
            int num = rs.getInt(1);
            assertThat(num, is(0));
        } catch (SQLException e) {
            assertThat(e.toString(), true, is(false));
        }
    }
    
    @Then("a file exists in the directory {string} with extension {string}")
    public void aFileExistsInTheDirectoryWithExtension(String path, String extension) {
        File keydir = new File(path);
        boolean extensionsMatch = false;
        for (File file: keydir.listFiles()) {
            if (!file.isDirectory()) {
                String actualExtension = "";
                try {
                    String name = file.getName();
                    actualExtension = name.substring(name.lastIndexOf(".") + 1);
                } catch (Exception e) {
                    actualExtension = "";
                }
                if (actualExtension.equals(extension)) {
                    extensionsMatch = true;
                }
            }
        }
        assertThat(extensionsMatch, is(true));
    }
    
    @Then("a file exists in the directory {string} with name {string}")
    public void aFileExistsInTheDirectoryWithName(String path, String name) {
        File keydir = new File(path);
        boolean namesMatch = false;
        for (File file: keydir.listFiles()) {
            if (!file.isDirectory()) {
                String actualName = "";
                try {
                    actualName = file.getName();
                } catch (Exception e) {
                    actualName = "";
                }
                if (actualName.equals(name)) {
                    namesMatch = true;
                }
            }
        }
        assertThat(namesMatch, is(true));
    }

    @Then("the wallet {string} should contain {int} WoCoins")
    public void theWalletShouldContainWoCoins(String pubkey, Integer amount) {
        BigInteger num = Utilities.getBalance(pubkey);
        assertThat(num, is(BigInteger.valueOf(amount)));
    }

}
