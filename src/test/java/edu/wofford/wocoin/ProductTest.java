package edu.wofford.wocoin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProductTest {
    private Product skittles1;
    private Product chalk2;
    private Product zombieland3;
    private Product apple4;
    private Product paper5;
    private Product risk6;
    private Product tripToCharlotte7;

    private ArrayList<Product> products;


    @Before
    public void setUp() throws Exception {
        /*
          The correct ordering of the items is seen below;
          1: >>>  skittles: a half-eaten bag  [1 WoCoin]
          2: chalk: taken from a classroom  [2 WoCoins]
          3: >>>  Zombieland: DVD  [2 WoCoins]
          4: apple: small  [3 WoCoins]
          5: paper: a ream for a printer  [4 WoCoins]
          6: >>>  Risk: board game  [4 WoCoins]
          7: trip to Charlotte: no questions asked  [4 WoCoins]
         */

        skittles1 = new Product("testuser", 1, "skittles", "a half-eaten bag");
        chalk2 = new Product("nottestuser", 2, "chalk", "taken from a classroom");
        zombieland3 = new Product("testuser", 2, "Zombieland", "DVD");
        apple4 = new Product("nottestuser", 3, "apple", "small");
        paper5 = new Product("nottestuser", 4, "paper", "a ream for a printer");
        risk6 = new Product("testuser", 4, "Risk", "board game");
        tripToCharlotte7 = new Product("nottestuser", 4, "trip to Charlotte", "no questions asked");

        products = new ArrayList<>();

        products.add(skittles1);
        products.add(chalk2);
        products.add(zombieland3);
        products.add(apple4);
        products.add(paper5);
        products.add(risk6);
        products.add(tripToCharlotte7);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToString() {
        /*
          The correct ordering of the items is seen below;
          1: >>>  skittles: a half-eaten bag  [1 WoCoin]
          2: chalk: taken from a classroom  [2 WoCoins]
          3: >>>  Zombieland: DVD  [2 WoCoins]
          4: apple: small  [3 WoCoins]
          5: paper: a ream for a printer  [4 WoCoins]
          6: >>>  Risk: board game  [4 WoCoins]
          7: trip to Charlotte: no questions asked  [4 WoCoins]
         */
        skittles1.setCurrentUser("testuser");
        skittles1.setDisplayType(Product.DisplayType.SHOWCURRENTUSER);
        chalk2.setCurrentUser("testuser");
        zombieland3.setCurrentUser("testuser");
        apple4.setCurrentUser("testuser");
        risk6.setCurrentUser("testuser");

        assertEquals(">>>  skittles: a half-eaten bag  [1 WoCoin]", skittles1.toString());
        assertEquals("chalk: taken from a classroom  [2 WoCoins]", chalk2.toString());
        assertEquals("Zombieland: DVD  [2 WoCoins]", zombieland3.toString());
        assertEquals("apple: small  [3 WoCoins]", apple4.toString());
        assertEquals("paper: a ream for a printer  [4 WoCoins]", paper5.toString());
        assertEquals("Risk: board game  [4 WoCoins]", risk6.toString());
        assertEquals("trip to Charlotte: no questions asked  [4 WoCoins]", tripToCharlotte7.toString());
    }

    @Test
    public void compareTo() {

        // Test that the order of the items when comparing by price first is correct
        products.get(0).setCompareType(Product.CompareType.PRICE);

        for (int i = 0; i < products.size() - 1; i++) {
            products.get(i + 1).setCompareType(Product.CompareType.PRICE);
            assertTrue(products.get(i).compareTo(products.get(i + 1)) < 0);
            assertEquals(0, products.get(i).compareTo(products.get(i)));
            assertTrue(products.get(i + 1).compareTo(products.get(i)) > 0);
        }

        for (Product product : products) {
            product.setCompareType(Product.CompareType.ALPHABETICALLY);
            assertEquals(0, product.compareTo(product));
        }

        assertTrue(apple4.compareTo(chalk2) < 0);
        assertTrue(chalk2.compareTo(paper5) < 0);
        assertTrue(paper5.compareTo(risk6) < 0);
        assertTrue(risk6.compareTo(skittles1) < 0);
        assertTrue(skittles1.compareTo(tripToCharlotte7) < 0);
        assertTrue(tripToCharlotte7.compareTo(zombieland3) < 0);

        assertTrue(zombieland3.compareTo(tripToCharlotte7) > 0);
        assertTrue(tripToCharlotte7.compareTo(skittles1) > 0);
        assertTrue(skittles1.compareTo(risk6) > 0);
        assertTrue(risk6.compareTo(paper5) > 0);
        assertTrue(paper5.compareTo(chalk2) > 0);
        assertTrue(chalk2.compareTo(apple4) > 0);
    }

    @Test
    public void testSortingListWorksCorrectly() {
        for(Product product : products) {
            product.setDisplayType(Product.DisplayType.SHOWCURRENTUSER);
            product.setCompareType(Product.CompareType.PRICE);
            product.setCurrentUser("testuser");
        }

        products.sort(Product::compareTo);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < products.size() - 1; i++){
            sb.append(String.format("%d: %s\n", i + 1, products.get(i).toString()));
        }
        sb.append(String.format("%d: %s", products.size(), products.get(products.size() - 1)));

        String expected = "1: >>>  skittles: a half-eaten bag  [1 WoCoin]\n" +
                          "2: chalk: taken from a classroom  [2 WoCoins]\n" +
                          "3: >>>  Zombieland: DVD  [2 WoCoins]\n" +
                          "4: apple: small  [3 WoCoins]\n" +
                          "5: paper: a ream for a printer  [4 WoCoins]\n" +
                          "6: >>>  Risk: board game  [4 WoCoins]\n" +
                          "7: trip to Charlotte: no questions asked  [4 WoCoins]";

        assertEquals(expected, sb.toString());
    }
}