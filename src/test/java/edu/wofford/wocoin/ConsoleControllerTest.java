package edu.wofford.wocoin;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.testng.Assert.assertEquals;

import java.sql.*;

public class ConsoleControllerTest {

    private SQLController sqlController;

    @BeforeClass
    public static void rebuildTestDB() {
        File file = new File("test.db");
        if (file.exists()) {
            boolean delete = file.delete();
        }

        Utilities.createTestDatabase("test.db");
    }

    @Before
    public void setUp(){
        sqlController = new SQLController("test.db");
    }

    @AfterClass
    public static void tearDown() {
        File file = new File("test.db");
        if (file.exists()) {
            boolean delete = file.delete();
        }
    }

    @Test
    public void testAddUser() {
        ConsoleController cm = new ConsoleController(sqlController);
        cm.adminLogin("adminpwd");
        assertEquals(cm.addUser("testadduser", "test"), "testadduser was added.");
        assertEquals(cm.addUser("testadduser", "test"), "testadduser already exists.");
    }

    @Test
    public void testRemoveUser() {
        ConsoleController cm = new ConsoleController(sqlController);
        cm.adminLogin("adminpwd");
        cm.addUser("testadduser", "test");
        assertEquals(cm.removeUser("testadduser"), "testadduser was removed.");
        assertEquals(cm.removeUser("testadduser"), "testadduser does not exist.");
    }

    @Test
    public void testUserLogin() {
        sqlController.insertUser("testlogin", "testpass");
        ConsoleController cm = new ConsoleController(sqlController);
        assertFalse(cm.userLogin("baduser", "badpass"));
        assertTrue(cm.userLogin("testlogin", "testpass"));
        cm.doLogout();
    }

    @Test
    public void testAddWallet() {
        sqlController.insertUser("testwallet", "testpass");
        ConsoleController cm = new ConsoleController(sqlController);
        cm.userLogin("testwallet", "testpass");
        assertEquals(cm.getCurrentUser(), "testwallet");
    }

    @Test
    public void testUserWithWallet() {
        sqlController.insertUser("testuserwithwallet", "testpassword");
        ConsoleController cm = new ConsoleController(sqlController);
        assertFalse(cm.userHasWallet());
        assertTrue(cm.userLogin("testuserwithwallet", "testpassword"));
        assertFalse(cm.userHasWallet());
        sqlController.addWallet("testuserwithwallet", "testkey");
        assertTrue(cm.userHasWallet());
        assertTrue(cm.deleteUserWallet());
        cm.doLogout();
        cm.removeUser("testuserwithwallet");
    }

    @Test
    public void testWalletCreation() {
        sqlController.insertUser("testwalletcreate", "test");
        ConsoleController cm = new ConsoleController(sqlController);
        assertSame(WalletUtilities.CreateWalletResult.FAILED, cm.addWalletToUser("nouser"));
        assertTrue(cm.userLogin("testwalletcreate", "test"));
        assertSame(WalletUtilities.CreateWalletResult.SUCCESS, cm.addWalletToUser("test/"));
        File file = new File("test/testwalletcreate/mykeyfile.json");
        try {
            file.createNewFile();
        } catch (IOException e) { }
        assertSame(WalletUtilities.CreateWalletResult.FILEALREADYEXISTS, cm.addWalletToUser("test/"));
        assertSame(WalletUtilities.CreateWalletResult.SUCCESS, cm.addWalletToUser("test/test/"));
        assertTrue(sqlController.findWallet("testwalletcreate"));

        try {
            FileUtils.deleteDirectory(new File("test/"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testAddNewProduct() {
        sqlController.insertUser("paul","Wofford1854");
        sqlController.insertUser("john","Wofford1854");
        sqlController.addWallet("john","j12345");
        ConsoleController cm = new ConsoleController(sqlController);

        cm.userLogin("paul", "Wofford1854");
        assertEquals(cm.addNewProduct("testitem","This is the description.", 20), "User has no wallet.");

        cm.userLogin("john", "Wofford1854");

        Product newProduct = new Product("john", 20, "testitem", "testdescription");
        assertEquals(cm.addNewProduct("testitem", "testdescription", 20), "Product added.");
        assertTrue(sqlController.productExistsInDatabase(newProduct));

        assertEquals(cm.addNewProduct("", "testdescription", 20), "Invalid value.\nExpected a string with at least 1 character.");
        assertEquals(cm.addNewProduct("testitem", "", 20), "Invalid value.\nExpected a string with at least 1 character.");
        assertEquals(cm.addNewProduct("testitem", "testdescription", 0), "Invalid value.\nExpected an integer value greater than or equal to 1.");
        assertEquals(cm.addNewProduct("testitem", "testdescription", -1), "Invalid value.\nExpected an integer value greater than or equal to 1.");
    }

    @Test
    public void testRemoveProduct() {
        sqlController.insertUser("paul","Wofford1854");
        sqlController.insertUser("john","Wofford1854");
        sqlController.addWallet("john","j12345");
        ConsoleController cm = new ConsoleController(sqlController);


        Product newProduct = new Product("john", 20, "testitem", "testdescription");
        cm.userLogin("paul", "Wofford1854");
        assertEquals(cm.removeProduct(new Product("paul", 20, "testitem", "testdescription")), "User has no wallet.");

        cm.userLogin("john", "Wofford1854");
        cm.addNewProduct(newProduct.getName(), newProduct.getDescription(), newProduct.getPrice());

        assertEquals(cm.removeProduct(newProduct), "Product removed.");
    }

    @Test
    public void testGetUserProducts() {
        ConsoleController cc = new ConsoleController(sqlController);

        Product skittles1 = new Product("jsmith", 1, "skittles", "a half-eaten bag");
        Product chalk2 = new Product("jdoe", 2, "chalk", "taken from a classroom");
        Product zombieland3 = new Product("jsmith", 2, "Zombieland", "DVD");
        Product apple4 = new Product("jdoe", 3, "apple", "small");
        Product paper5 = new Product("jdoe", 4, "paper", "a ream for a printer");
        Product risk6 = new Product("jsmith", 4, "Risk", "board game");
        Product tripToCharlotte7 = new Product("jdoe", 4, "trip to Charlotte", "no questions asked");

        ArrayList<Product> expectedJsmithProducts = new ArrayList<>();
        ArrayList<Product> expectedJdoeProducts = new ArrayList<>();

        expectedJdoeProducts.add(chalk2);
        expectedJdoeProducts.add(apple4);
        expectedJdoeProducts.add(paper5);
        expectedJdoeProducts.add(tripToCharlotte7);

        expectedJsmithProducts.add(skittles1);
        expectedJsmithProducts.add(zombieland3);
        expectedJsmithProducts.add(risk6);

        expectedJsmithProducts.sort(Product::compareTo);
        expectedJdoeProducts.sort(Product::compareTo);

        assertTrue(cc.userLogin("jdoe", "jdoe"));
        ArrayList<Product> actualJdoeProducts = cc.getUserProducts();

        cc.userLogin("jsmith", "jsmith");
        ArrayList<Product> actualJsmithProducts = cc.getUserProducts();

        actualJdoeProducts.sort(Product::compareTo);
        actualJsmithProducts.sort(Product::compareTo);

        assertEquals(expectedJsmithProducts, actualJsmithProducts);

        assertEquals(expectedJdoeProducts, actualJdoeProducts);

    }

    @Test
    public void testGetAllProducts() {
        rebuildTestDB();
        ConsoleController cc = new ConsoleController(sqlController);

        Product skittles1 = new Product("jsmith", 1, "skittles", "a half-eaten bag");
        Product chalk2 = new Product("jdoe", 2, "chalk", "taken from a classroom");
        Product zombieland3 = new Product("jsmith", 2, "Zombieland", "DVD");
        Product apple4 = new Product("jdoe", 3, "apple", "small");
        Product paper5 = new Product("jdoe", 4, "paper", "a ream for a printer");
        Product risk6 = new Product("jsmith", 4, "Risk", "board game");
        Product tripToCharlotte7 = new Product("jdoe", 4, "trip to Charlotte", "no questions asked");

        ArrayList<Product> expectedProducts = new ArrayList<>();

        expectedProducts.add(skittles1);
        expectedProducts.add(chalk2);
        expectedProducts.add(zombieland3);
        expectedProducts.add(apple4);
        expectedProducts.add(paper5);
        expectedProducts.add(risk6);
        expectedProducts.add(tripToCharlotte7);

        ArrayList<Product> actualProducts = cc.getAllProducts();

        expectedProducts.sort(Product::compareToWithPrice);
        actualProducts.sort(Product::compareToWithPrice);

        assertEquals(expectedProducts, actualProducts);

    }

    @Test
    public void testGetSQLController() {
        ConsoleController cc = new ConsoleController(sqlController);
        assertSame(cc.getSqlController(), sqlController);
    }

    @Test
    public final void buyProductWithoutWallet(){
        ConsoleController cc = new ConsoleController(sqlController);
        String tmp = cc.buyProduct("",new Product("john", 20, "x", "This is the description."));
        assertEquals("User has no wallet.", tmp);
    }

    @Test
    public final void buyProductWithBadWallet(){
        sqlController.insertUser("userWithBadWallet", "xyz");
        sqlController.addWallet("userWithBadWallet","0x1234");
        ConsoleController cc = new ConsoleController(sqlController);
        cc.userLogin("userWithBadWallet","xyz");
        String tmp = cc.buyProduct("",new Product("john", 20, "x", "This is the description."));
        assertEquals("Invalid wallet.", tmp);
    }

    @Test
    public final void sendAndDeleteMessage(){
        sqlController.insertUser("user7","abc");
        sqlController.insertUser("user8","123");
        Product newProduct = new Product("user7", 20, "y", "This is not product x.");
        sqlController.addProduct(newProduct);
        sqlController.addWallet("user7","7");
        sqlController.addWallet("user8","8");

        int nextId = -1;

        assertEquals(SQLController.AddProductResult.ADDED, sqlController.addProduct(newProduct));

        try(Connection dataConn = DriverManager.getConnection(sqlController.getPath())){
            PreparedStatement stSelect = dataConn.prepareStatement("Select max(id) from products");
            ResultSet dtr = stSelect.executeQuery();
            nextId = dtr.getInt(1);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        Product newProductWithId = new Product(nextId,"user7", 20, "y", "This is not product x.");
        ConsoleController cc = new ConsoleController(sqlController);
        cc.userLogin("user8","123");
        assertEquals("Message sent.",cc.sendMessage(newProductWithId,"This is a test."));

        try(Connection dataConn = DriverManager.getConnection(sqlController.getPath())){
            PreparedStatement stSelect = dataConn.prepareStatement("Select max(id) from messages");
            ResultSet dtr = stSelect.executeQuery();
            nextId = dtr.getInt(1);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        Message messageToDelete = new Message(nextId,"user8","user7","This is a test.","",newProductWithId);
        assertEquals("Message deleted.",cc.deleteMessage(messageToDelete));
    }

    @Test
    public final void getUserMessages(){
        ConsoleController cc = new ConsoleController(sqlController);
        cc.userLogin("user7","abc");
        assertEquals(sqlController.getMessagesForUser("user7"),cc.getUserMessages());
    }

    @Test
    public void testTransferWoCoins() {
        ConsoleController cc = new ConsoleController(sqlController);
        cc.addUser("testtransfer", "password");
        cc.addUser("testtransfernowallet", "password");
        cc.userLogin("testtransfer", "password");
        cc.addWalletToUser("test");
        assertEquals(cc.transferWocoinsToUser("testtransfer", 10), "Transfer complete.");
        assertEquals(cc.transferWocoinsToUser("notauser", 10), "No such user.");
        assertEquals(cc.transferWocoinsToUser("testtransfernowallet", 10), "User has no wallet.");
        assertEquals(cc.transferWocoinsToUser("testtransfer", -10), "Expected an integer value greater than or equal to 1.");

        // TODO When the transfer in SQLController is done, check to make sure the balance correctly reflects the transaction
    }
}
