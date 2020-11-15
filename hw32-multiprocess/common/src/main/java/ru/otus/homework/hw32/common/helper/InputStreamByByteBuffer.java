package ru.otus.homework.hw32.common.helper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class InputStreamByByteBuffer extends InputStream {

    private final ByteBuffer buffer;

    public InputStreamByByteBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (!buffer.hasRemaining()) {
            return -1;
        }
        int realLen = buffer.remaining() < len ? buffer.remaining() : len;
        buffer.get(b, off, realLen);
        return realLen;
    }

    @Override
    public int read() {
        return buffer.hasRemaining() ? buffer.get() : -1;
    }
}
