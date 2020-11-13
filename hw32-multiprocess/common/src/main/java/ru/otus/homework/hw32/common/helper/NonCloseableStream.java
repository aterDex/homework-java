package ru.otus.homework.hw32.common.helper;

import java.io.IOException;
import java.io.InputStream;

public class NonCloseableStream extends InputStream {

    private final InputStream is;

    public NonCloseableStream(InputStream is) {
        this.is = is;
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return is.read(b, off, len);
    }

    @Override
    public boolean markSupported() {
        return is.markSupported();
    }

    @Override
    public synchronized void mark(int readlimit) {
        is.mark(readlimit);
    }
}
