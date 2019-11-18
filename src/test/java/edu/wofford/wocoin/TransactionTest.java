package edu.wofford.wocoin;

import org.junit.*;
import static org.junit.Assert.*;

public class TransactionTest {

    @Test
    public final void transactionSuccessful() {
        try {
            Transaction.run();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
