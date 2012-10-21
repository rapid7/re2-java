package com.logentries.re2;

/*
 * Inspired by https://github.com/zeromq/jzmq/tree/master/src/org/zeromq .
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class EmbeddedLibraryTools {
    public static final boolean LOADED_RE2;
    public static final boolean LOADED_RE2_JAVA;

    static {
        LOADED_RE2 = loadEmbeddedLibrary("libre2");
        LOADED_RE2_JAVA = LOADED_RE2 && loadEmbeddedLibrary("libre2-java");
    }

    public static String getCurrentPlatformIdentifier() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            osName = "Windows";
        }
        return System.getProperty("os.arch") + "/" + osName;
    }

    private static boolean loadEmbeddedLibrary(final String name) {
        // attempt to locate embedded native library within JAR at following location:
        // /NATIVE/${os.arch}/${os.name}/libre2{,-java}.[so|dylib|dll]
        String[] allowedExtensions = new String[] {"so", "dylib", "dll", };
        StringBuilder url = new StringBuilder();
        url.append("/NATIVE/");
        url.append(getCurrentPlatformIdentifier());
        url.append('/');
        url.append(name);
        url.append('.');
        URL nativeLibraryUrl = null;
        // loop through extensions, stopping after finding first one
        for (String ext : allowedExtensions) {
            System.err.println("Looking for native library: " + url.toString() + ext);
            nativeLibraryUrl = RE2.class.getResource(url.toString() + ext);
            if (nativeLibraryUrl != null)
                break;
            }
            //
            if (nativeLibraryUrl != null) {
                // native library found within JAR, extract and load
                try {
                    final File libfile = File.createTempFile(name, ".lib");
                    libfile.deleteOnExit(); // just in case
                    //
                    final InputStream in = nativeLibraryUrl.openStream();
                    final OutputStream out = new BufferedOutputStream(new FileOutputStream(libfile));
                    //
                    int len = 0;
                    byte[] buffer = new byte[8192];
                    while ((len = in.read(buffer)) > -1)
                        out.write(buffer, 0, len);
                        out.close();
                        in.close();
                        System.load(libfile.getAbsolutePath());
                        libfile.delete();
                        return true;
                    } catch (IOException x) {
                        // mission failed, do nothing
                    }
        } // nativeLibraryUrl exists
        return false;
    }

    private EmbeddedLibraryTools() {};
}
