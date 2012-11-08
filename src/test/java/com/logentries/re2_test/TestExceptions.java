package com.logentries.re2_test;

import com.logentries.re2.RE2;
import com.logentries.re2.Options;
import com.logentries.re2.RegExprException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TestExceptions {
    @Test
    public void testCorrect() {
        try {
            assertNotNull(new RE2("Everything Works"));
        } catch (RegExprException e) {
            fail();
        }
    }

    @Test(expected=RegExprException.class)
    public void testWrong() throws RegExprException {
        try {
            RE2 re2 = new RE2("(Nothing Works", new Options().setLogErrors(false));
            System.err.println("re2 = " + re2);
        } catch (RegExprException e) {
            System.err.println("Exdeption thrown, msg: " + e.getMessage());
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

