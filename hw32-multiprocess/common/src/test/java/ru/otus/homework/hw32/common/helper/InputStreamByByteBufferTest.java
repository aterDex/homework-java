package ru.otus.homework.hw32.common.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InputStreamByByteBufferTest {

    private static final int SIZE_TEST = 100;
    private InputStream inputStream;
    private byte[] source;

    @BeforeEach
    void init() {
        source = new byte[SIZE_TEST];
        for (int i = 0; i < source.length; i++) {
            source[i] = (byte) i;
        }
        var buffer = ByteBuffer.wrap(source);
        inputStream = new InputStreamByByteBuffer(buffer);
    }

    @Test
    void read() throws IOException {
        for (int i = 0; i < SIZE_TEST; i++) {
            assertEquals(i, inputStream.read());
        }
        assertEquals(-1, inputStream.read());
        assertEquals(-1, inputStream.read());
    }

    @Test
    void readToArray1() throws IOException {
        byte[] target = new byte[SIZE_TEST];
        int off = 0;
        while (true) {
            int read = inputStream.read(target, off, 10);
            if (read < 0) {
                break;
            }
            assertEquals(10, read);
            off += 10;
        }
        inputStream.read(target, 0, target.length);
        assertArrayEquals(source, target);
    }

    @Test
    void readToArray2() throws IOException {
        byte[] target = new byte[SIZE_TEST];
        int size = SIZE_TEST / 2 + SIZE_TEST / 4;
        int read = inputStream.read(target, 0, size);
        assertEquals(read, size);
        read = inputStream.read(target, size, size);
        assertEquals(SIZE_TEST - size, read);
        assertEquals(-1, inputStream.read(target, 0, size));
        assertArrayEquals(source, target);
    }
}