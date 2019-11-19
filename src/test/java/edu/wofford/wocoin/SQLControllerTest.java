package edu.wofford.wocoin;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class SQLControllerTest {

    // TODO change this variable name, smh
    private SQLController foobar;

    @Before
    public void setup(){
        foobar = new SQLController();
    }

    @BeforeClass
    public static void setupDB(){
        new File("wocoinDatabase.sqlite3").delete();
        Utilities.createTestDatabase("wocoinDatabase.sqlite3");
    }

    /*@AfterClass
    public static void destroyDB(){
        new File("wocoinDatabase.sqlite3").delete();
    }*/

    @Test
    public final void testConstructor(){
        SQLController bar = new SQLController("testDB.sqlite3");
        assertEquals("jdbc:sqlite:testDB.sqlite3", bar.getPath());

        assertEquals("jdbc:sqlite:wocoinDatabase.sqlite3", foobar.getPath());
    }


    @Test
    public final void walletExists(){
        assertTrue(foobar.findWallet("srogers"));
    }

    @Test
    public final void walletNotExists(){
        assertFalse(foobar.findWallet("tstark"));
    }

    @Test
    public final void addWallet(){
        foobar.removeWallet("test");
        assertEquals(SQLController.AddWalletResult.ADDED, foobar.addWallet("test","8675309"));
    }

    @Test
    public final void addWalletDuplicate(){
        foobar.addWallet("test","8675309");
        assertEquals(SQLController.AddWalletResult.ALREADYEXISTS, foobar.addWallet("test","8675309"));
    }

    @Test
    public final void replaceWallet(){
        foobar.addWallet("test","8675309");
        assertEquals(SQLController.ReplaceWalletResult.REPLACED, foobar.replaceWallet("test","867530"));
        assertEquals("867530", foobar.retrievePublicKey("test"));
    }

    @Test
    public final void replaceNonExistentWallet(){
        assertEquals(SQLController.ReplaceWalletResult.NOSUCHWALLET, foobar.replaceWallet("tstark","86753099"));
    }

    @Test
    public final void removeWallet(){
        foobar.addWallet("bbanner","q8675309");
        assertEquals(SQLController.RemoveWalletResult.REMOVED, foobar.removeWallet("bbanner"));
    }

    @Test
    public final void removeNonExistentWallet(){
        foobar.removeWallet("bbanner");
        assertEquals(SQLController.RemoveWalletResult.NOSUCHWALLET, foobar.removeWallet("bbanner"));
    }

    @Test
    public final void getPublicKeyTest(){
        foobar.removeWallet("nfury");
        foobar.addWallet("nfury","nf675309");
        assertEquals("nf675309", foobar.retrievePublicKey("nfury"));
    }

    @Test
    public final void publicKeyDoesNotExist(){
        foobar.removeWallet("nfury");
        assertEquals("", foobar.retrievePublicKey("nfury"));
    }

    @Test
    public final void getName(){
        assertEquals("jdoe", foobar.getName("587888ea2b080656816aad7e0bc8f1cf3cf0bced"));
    }

    @Test
    public final void getNameInvalidPublicKey(){
        assertEquals("", foobar.getName("test"));
    }

    @Test
    public final void successfulProductAdd(){
        foobar.insertUser("john","Wofford1854");
        foobar.addWallet("john","j12345");

        Product newProduct = new Product("john", 20, "x", "This is the description.");
        assertEquals(SQLController.AddProductResult.ADDED, foobar.addProduct(newProduct));
        try (Connection dataConn = DriverManager.getConnection(foobar.getPath())) {
            PreparedStatement stSelect = dataConn.prepareStatement("SELECT * FROM products order by id desc limit 1");
            ResultSet dtr = stSelect.executeQuery();
            assertEquals("j12345", dtr.getString(2));
            assertEquals(20, dtr.getInt(3));
            assertEquals("x", dtr.getString(4));
            assertEquals("This is the description.", dtr.getString(5));
        } catch (Exception e) {
            System.out.println("HERE" + e.toString());
        }
    }

    @Test
    public final void findAddedProduct() {
        Product validProduct = new Product("jsmith", 1, "skittles", "a half-eaten bag");
        Product nonExistentProduct = new Product("nottestuser", 2, "chalk", "taken from a classroom");

        assertTrue(foobar.productExistsInDatabase(validProduct));
        assertFalse(foobar.productExistsInDatabase(nonExistentProduct));

    }

    @Test
    public final void ProductAddWithoutWallet(){
        foobar.insertUser("newUser","password");
        Product noWalletProduct = new Product("newUser", 20, "x", "This is the description");
        assertEquals(SQLController.AddProductResult.NOWALLET, foobar.addProduct(noWalletProduct));
    }

    @Test
    public final void ProductAddNoUser(){
        Product noUserProduct = new Product("noName", 20, "x", "This is the description");
        assertEquals(SQLController.AddProductResult.NOWALLET, foobar.addProduct(noUserProduct));
    }

    @Test
    public final void ProductAddNoDescription(){
        Product noDescriptionProduct = new Product("jsmith", 20, "x", "");
        assertEquals(SQLController.AddProductResult.EMPTYDESCRIPTION, foobar.addProduct(noDescriptionProduct));
    }

    @Test
    public final void ProductAddNegativePrice(){
        Product negativePriceProduct = new Product("jsmith", -2, "x", "This is the description");
        assertEquals(SQLController.AddProductResult.NONPOSITIVEPRICE, foobar.addProduct(negativePriceProduct));
    }

    @Test
    public final void ProductAddZeroPrice(){
        Product zeroPriceProduct = new Product("jsmith", 0, "x", "This is the description");
        assertEquals(SQLController.AddProductResult.NONPOSITIVEPRICE, foobar.addProduct(zeroPriceProduct));
    }

    @Test
    public final void ProductAddNoName(){
        Product noNameProduct = new Product("jsmith", 20, "", "This is the description");
        assertEquals(SQLController.AddProductResult.EMPTYNAME, foobar.addProduct(noNameProduct));
    }

    @Test
    public final void removeProductInDB() {
        foobar.insertUser("john","Wofford1854");
        foobar.addWallet("john","j12345");

        Product newProduct = new Product("john", 20, "x", "This is the description.");
        assertEquals(SQLController.AddProductResult.ADDED, foobar.addProduct(newProduct));

        assertEquals(SQLController.RemoveProductResult.REMOVED, foobar.removeProduct(newProduct));

        try (Connection dataConn = DriverManager.getConnection(foobar.getPath())) {
            PreparedStatement stSelect = dataConn.prepareStatement("SELECT COUNT(*) FROM products WHERE seller = ? AND price = ? AND name = ? AND description = ?");
            stSelect.setString(1, foobar.retrievePublicKey(newProduct.getSeller()));
            stSelect.setInt(2, newProduct.getPrice());
            stSelect.setString(3, newProduct.getName());
            stSelect.setString(4, newProduct.getDescription());
            ResultSet dtr = stSelect.executeQuery();
            assertEquals(0, dtr.getInt(1));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public final void removeProductNoWallet() {
        Product newProduct = new Product("nowallet", 20, "x", "This is the description.");
        assertEquals(SQLController.RemoveProductResult.NOWALLET, foobar.removeProduct(newProduct));
    }

    @Test
    public final void removeProductNoExists() {
        Product newProduct = new Product("jsmith", 20, "x", "This is the description.");
        assertEquals(SQLController.RemoveProductResult.DOESNOTEXIST, foobar.removeProduct(newProduct));
    }

    @Test
    public final void getUserProductsFromDB() {
        setupDB();
        Product skittles1 = new Product("jsmith", 1, "skittles", "a half-eaten bag");
        Product chalk2 = new Product("jdoe", 2, "chalk", "taken from a classroom");
        Product zombieland3 = new Product("jsmith", 2, "Zombieland", "DVD");
        Product apple4 = new Product("jdoe", 3, "apple", "small");
        Product paper5 = new Product("jdoe", 4, "paper", "a ream for a printer");
        Product risk6 = new Product("jsmith", 4, "Risk", "board game");
        Product tripToCharlotte7 = new Product("jdoe", 4, "trip to Charlotte", "no questions asked");

        ArrayList<Product> expectedJdoeProducts = new ArrayList<>();
        ArrayList<Product> expectedJsmithProducts = new ArrayList<>();

        expectedJdoeProducts.add(chalk2);
        expectedJdoeProducts.add(apple4);
        expectedJdoeProducts.add(paper5);
        expectedJdoeProducts.add(tripToCharlotte7);

        expectedJsmithProducts.add(skittles1);
        expectedJsmithProducts.add(zombieland3);
        expectedJsmithProducts.add(risk6);

        expectedJdoeProducts.sort(Product::compareTo);
        expectedJsmithProducts.sort(Product::compareTo);

        ArrayList<Product> actualJdoeProducts = foobar.getUserProductsList("jdoe");
        ArrayList<Product> actualJsmithProducts = foobar.getUserProductsList("jsmith");

        actualJdoeProducts.sort(Product::compareTo);
        actualJsmithProducts.sort(Product::compareTo);

        assertEquals(expectedJdoeProducts, actualJdoeProducts);

        assertEquals(expectedJsmithProducts, actualJsmithProducts);

        assertEquals(new ArrayList<Product>(), foobar.getUserProductsList("notauser"));

    }

    @Test
    public final void getPurchasableProducts(){
        setupDB();
        Product skittles1 = new Product("jsmith", 1, "skittles", "a half-eaten bag");
        Product zombieland3 = new Product("jsmith", 2, "Zombieland", "DVD");
        Product risk6 = new Product("jsmith", 4, "Risk", "board game");

        ArrayList<Product> expectedJdoeProducts = new ArrayList<>();

        expectedJdoeProducts.add(skittles1);
        expectedJdoeProducts.add(zombieland3);
        expectedJdoeProducts.add(risk6);

        expectedJdoeProducts.sort(Product::compareTo);

        ArrayList<Product> actualJdoeProducts = foobar.getPurchasableProductsList("jdoe",5);

        actualJdoeProducts.sort(Product::compareTo);

        assertEquals(expectedJdoeProducts, actualJdoeProducts);
    }

    @Test
    public final void getPurchasableProductsOther(){
        //setupDB();
        Product skittles1 = new Product("jsmith", 1, "skittles", "a half-eaten bag");
        Product zombieland3 = new Product("jsmith", 2, "Zombieland", "DVD");

        ArrayList<Product> expectedJdoeProducts = new ArrayList<>();

        expectedJdoeProducts.add(skittles1);
        expectedJdoeProducts.add(zombieland3);

        expectedJdoeProducts.sort(Product::compareTo);

        ArrayList<Product> actualJdoeProducts = foobar.getPurchasableProductsList("jdoe",2);

        actualJdoeProducts.sort(Product::compareTo);

        assertEquals(expectedJdoeProducts, actualJdoeProducts);
    }

    @Test
    public final void getPurchasableProductsOtherOther(){
        //setupDB();
        ArrayList<Product> expectedJdoeProducts = new ArrayList<>();
        ArrayList<Product> actualJdoeProducts = foobar.getPurchasableProductsList("jdoe",0);
        assertEquals(expectedJdoeProducts, actualJdoeProducts);
    }

    @Test
    public void getAllProductsFromDB() {
        setupDB();
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

        ArrayList<Product> actualProducts = foobar.getAllProductsList();

        expectedProducts.sort(Product::compareToWithPrice);
        actualProducts.sort(Product::compareToWithPrice);

        assertEquals(expectedProducts, actualProducts);
    }

    @Test
    public void transferSuccess(){
        foobar.insertUser("test", "1234");
        foobar.removeWallet("test");
        foobar.addWallet("test","8675309");
        assertEquals(SQLController.TransferWocoinResult.SUCCESS, foobar.transferWocoin("test", 5));
    }
    @Test
    public void transferNoUser(){
        assertEquals(SQLController.TransferWocoinResult.NOUSER, foobar.transferWocoin("jonDoe", 5));
    }

    @Test
    public void transferNoWallet(){
        foobar.insertUser("test","1234");
        foobar.removeWallet("test");
        assertEquals(SQLController.TransferWocoinResult.NOWALLET, foobar.transferWocoin("test", 5 ));
    }

    @Test
    public void transferNegValue(){
        foobar.insertUser("test", "1234");
        foobar.removeWallet("test");
        foobar.addWallet("test","8675309");
        assertEquals(SQLController.TransferWocoinResult.NEGATIVEINPUT, foobar.transferWocoin("test", -5));
    }
}
