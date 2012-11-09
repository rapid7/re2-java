package com.logentries.re2;

public class LibraryLoader {
    static {
        if (!EmbeddedLibraryTools.LOADED_RE2) {
            System.loadLibrary("re2");
        }
        if (!EmbeddedLibraryTools.LOADED_RE2_JAVA) {
            System.loadLibrary("re2-java");
        }
    }

    protected LibraryLoader() { }
}
