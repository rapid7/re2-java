package com.logentries.re2_test;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GenString {
    private List<String> mAlphabet;
    private int mMaxLen;
    private Random mRand = new Random();

    public GenString(final List<String> alphabet, final int maxLen) {
        mAlphabet = new ArrayList<String>(alphabet);
        mMaxLen = maxLen;
    }

    public String next() {
        final int len = mRand.nextInt(100) == 0 ? mRand.nextInt(mMaxLen) : mRand.nextInt(mMaxLen - 1) + 1;
        final int asize = mAlphabet.size();
        String ret = new String();
        for (int i = 0; i < len; ++i) {
            ret += mAlphabet.get(mRand.nextInt(asize));
        }
        return ret;
    }
}
