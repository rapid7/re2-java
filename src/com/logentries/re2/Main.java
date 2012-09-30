package com.logentries.re2;

public class Main {
    public static void main(String[] args) {
        final boolean res1 = RE2.fullMatch("hello", "(h.*o)");
        System.err.println("res1 = " + res1);
        /* */
        final boolean res2 = RE2.fullMatch("hello", "(h.*x)");
        System.err.println("res2 = " + res2);
        /* */
        final RE2 re_x = new RE2("(h.*o)");
        final boolean res3 = re_x.fullMatch("hello");
        System.err.println("res3 = " + res3);
        final boolean res4 = re_x.fullMatch("hellx");
        System.err.println("res4 = " + res4);
        final RE2 re_y = new RE2("(h.*o)", new Options());
    }
}
