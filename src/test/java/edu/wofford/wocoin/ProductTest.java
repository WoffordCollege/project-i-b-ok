package edu.wofford.wocoin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProductTest {
    Product skittles1;
    Product chalk2;
    Product zombieland3;
    Product apple4;
    Product paper5;
    Product risk6;
    Product charlotteTrip7;


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
        charlotteTrip7 = new Product("nottestuser", 4, "trip to Charlotte", "no questions asked");
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
        chalk2.setCurrentUser("testuser");
        zombieland3.setCurrentUser("testuser");
        apple4.setCurrentUser("testuser");
        risk6.setCurrentUser("testuser");

        assertEquals(">>>  skittles: a half-eaten bag  [1 WoCoin]", skittles1.toString());
        assertEquals("chalk: taken from a classroom  [2 WoCoins]", chalk2.toString());
        assertEquals(">>>  Zombieland: DVD  [2 WoCoins]", zombieland3.toString());
        assertEquals("apple: small  [3 WoCoins]", apple4.toString());
        assertEquals("paper: a ream for a printer  [4 WoCoins]", paper5.toString());
        assertEquals(">>>  Risk: board game  [4 WoCoins]", risk6.toString());
        assertEquals("trip to Charlotte: no questions asked  [4 WoCoins]", charlotteTrip7.toString());
    }

    @Test
    public void compareTo() {
        assertTrue(skittles1.compareTo(chalk2) < 0);
        assertTrue(chalk2.compareTo(zombieland3) < 0);
        assertTrue(zombieland3.compareTo(apple4) < 0);
        assertTrue(apple4.compareTo(paper5) < 0);
        assertTrue(paper5.compareTo(risk6) < 0);
        assertTrue(risk6.compareTo(charlotteTrip7) < 0);

        assertEquals(0, skittles1.compareTo(skittles1));
        assertEquals(0, chalk2.compareTo(chalk2));
        assertEquals(0, zombieland3.compareTo(zombieland3));
        assertEquals(0, apple4.compareTo(apple4));
        assertEquals(0, paper5.compareTo(paper5));
        assertEquals(0, risk6.compareTo(risk6));
        assertEquals(0, charlotteTrip7.compareTo(charlotteTrip7));


        assertTrue(chalk2.compareTo(skittles1) > 0);
        assertTrue(zombieland3.compareTo(chalk2) > 0);
        assertTrue(apple4.compareTo(zombieland3) > 0);
        assertTrue(paper5.compareTo(apple4) > 0);
        assertTrue(risk6.compareTo(paper5) > 0);
        assertTrue(charlotteTrip7.compareTo(risk6) > 0);
    }
}