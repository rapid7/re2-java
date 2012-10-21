package com.logentries.re2_test;

import java.util.Arrays;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class Main {
    private static void testThreads() {
        Result result = JUnitCore.runClasses(TestThreads.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
    }

    private static void testRandomExpr() {
        Result result = JUnitCore.runClasses(TestRandomExpr.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
    }

    public static void main(String[] args) {
        testThreads();
        testRandomExpr();

/*
        System.err.println("Generating random sequences");
        for (int i = 0; i < 30; ++i) {
            System.err.println(new GenRegExpr(Arrays.asList("aaa", "b", "ccc"), 3, 12).next());
        }
        System.err.println("Generating random strings");
        final GenString gs = new GenString(Arrays.asList("aaa", "b", "ccc"), 12);
        for (int i = 0; i < 30; ++i) {
            System.err.println(gs.next());
        }
*/
    }
}
