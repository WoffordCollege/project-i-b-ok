package edu.wofford.wocoin;

import org.junit.*;
import java.math.BigInteger;
import java.math.BigInteger;

import static org.junit.Assert.*;

public class TransactionTest {

    /*@Test
    public final void transactionSuccessful() {
        try {
            Transaction.send();
        } catch (Exception e){
            e.printStackTrace();
        }
    }*/

    @Test
    public final void getBalanceSuccessful() {
        try {
            assertEquals("23",Transaction.getBalance("a615316333ba8622fd5bb60fe39758b3515f774d").toString(10));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
