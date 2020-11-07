package ru.otus.homework.hw32.common.helper;

import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HelperHw32Test {

    @Test
    void readObjectFromByteBuffers() throws Exception {
        TestObject object1 = new TestObject();
        object1.text = "EXAMPLE";
        TestObject object2 = new TestObject();
        object2.text = "EXAMPLE2";
        ByteBuffer buffer;
        try (var baos = new ByteArrayOutputStream()
        ) {
            try (var oos1 = new ObjectOutputStream(baos)) {
                oos1.writeObject(object1);
            }
            try (var oos2 = new ObjectOutputStream(baos)) {
                oos2.writeObject(object2);
            }
            buffer = ByteBuffer.wrap(baos.toByteArray());
        }
        assertEquals(object1, HelperHw32.readObjectFromByteBuffers(buffer));
        assertEquals(object2, HelperHw32.readObjectFromByteBuffers(buffer));
        assertThrows(EOFException.class, () -> HelperHw32.readObjectFromByteBuffers(buffer));
    }

    @Test
    void objectToByte() {
        TestObject object1 = new TestObject();
        object1.text = "EXAMPLE";

        byte[] objectBytes = HelperHw32.objectToByte(object1);
        assertEquals(object1, HelperHw32.readObjectFromByteBuffers(ByteBuffer.wrap(objectBytes)));
    }

    @EqualsAndHashCode
    private static class TestObject implements Serializable {
        private static final long serialVersionUID = 1L;

        public String text;
    }
}