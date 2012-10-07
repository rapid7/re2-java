package com.logentries.re2;

public class Main {
    public static void main(String[] args) {
        final boolean res1 = RE2.fullMatch("hello", "(h.*o)");
        System.err.println("res1=" + res1);
        /* */
        final boolean res2 = RE2.fullMatch("hello", "(h.*x)");
        System.err.println("res2=" + res2);
        /* */
        final RE2 re_x = new RE2("(h.*o)");
        final boolean res3 = re_x.fullMatch("hello");
        System.err.println("res3=" + res3);
        final boolean res4 = re_x.fullMatch("hellx");
        System.err.println("res4=" + res4);
        final RE2 re_y = new RE2("(h.*o)", new Options());
        /* */
        int[] out00 = new int[1];
        final boolean res5 = RE2.fullMatch("1256", "(\\d+)", out00);
        System.err.println("res5=" + res5 + "; out00[0]=" + out00[0]);
        /* */
        int[] out10 = new int[1];
        String[] out11 = new String[1];
        long[] out12 = new long[1];
        final boolean res6 = RE2.fullMatch("1256xsssx136985478256", "(\\d+)x(\\w+)x(\\d+)", out10, out11, out12);
        System.err.println("res6=" + res6 + "; out10[0]=" + out10[0] + "; out11[0]=" + out11[0] + "; out12[0]=" + out12[0]);
        /* */
        int[] out20 = new int[3];
        String[] out21 = new String[2];
        long[] out22 = new long[4];
        final boolean res7 = RE2.fullMatch("1256-34-567xstring-everythingworks@13-698-547-12345678256", "(\\d+)-(\\d+)-(\\d+)x(\\w+)-(\\w+)@(\\d+)-(\\d+)-(\\d+)-(\\d+)", out20, out21, out22);
        System.err.println("res7=" + res7 + "; out20[0]=" + out20[0] + "; out20[1]=" + out20[1] + "; out20[2]=" + out20[2] + 
                                            "; out21[0]=" + out21[0] + "; out21[1]=" + out21[1] +
                                            "; out22[0]=" + out22[0] + "; out22[1]=" + out22[1] + "; out22[2]=" + out22[2] + "; out22[3]=" + out22[3]);
    }
}
