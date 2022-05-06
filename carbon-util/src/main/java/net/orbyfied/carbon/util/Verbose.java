package net.orbyfied.carbon.util;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public interface Verbose {

    class VerboseOutputStreamWriter
            extends OutputStreamWriter
            implements Verbose {
        public VerboseOutputStreamWriter(@NotNull OutputStream out, @NotNull String charsetName) throws UnsupportedEncodingException {
            super(out, charsetName);
        }

        public VerboseOutputStreamWriter(@NotNull OutputStream out) {
            super(out);
        }

        public VerboseOutputStreamWriter(@NotNull OutputStream out, @NotNull Charset cs) {
            super(out, cs);
        }

        public VerboseOutputStreamWriter(@NotNull OutputStream out, @NotNull CharsetEncoder enc) {
            super(out, enc);
        }
    }

}
