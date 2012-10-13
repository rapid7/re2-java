package com.logentries.re2_test;

import java.util.Arrays;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class Main {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestThreads.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.err.println("Generating random sequences");
        for (int i = 0; i < 30; ++i) {
            System.err.println(new GenRegExpr(Arrays.asList("aaa", "b", "ccc"), 3, 12).generator());
        }
    }
}
