/*
 *      Java Bindings for the RE2 Library
 *
 *      (c) 2012 Daniel Fiala <danfiala@ucw.cz>
 *
 */

package com.logentries.re2;

public class RegExprException extends Exception {
    public RegExprException(final String msg) {
        super(msg);
    }
}
