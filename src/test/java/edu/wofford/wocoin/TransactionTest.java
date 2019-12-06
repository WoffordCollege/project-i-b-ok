package edu.wofford.wocoin;

import org.junit.*;
import java.math.BigInteger;
import java.math.BigInteger;

import static org.junit.Assert.*;

public class TransactionTest {

    @Test
    public final void transactionSuccessful() {
        try {
            Transaction.send();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public final void getBalanceSuccessful() {
        try {
            String num = Transaction.getBalance("a615316333ba8622fd5bb60fe39758b3515f774d");
            int result = Integer.parseInt(num);
            assertEquals(result,Integer.parseInt(Transaction.getBalance("a615316333ba8622fd5bb60fe39758b3515f774d")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
