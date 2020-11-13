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

class HelperSerializeObjectTest {

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
        assertEquals(object1, HelperSerializeObject.readObjectFromByteBuffers(buffer));
        assertEquals(object2, HelperSerializeObject.readObjectFromByteBuffers(buffer));
        assertThrows(EOFException.class, () -> HelperSerializeObject.readObjectFromByteBuffers(buffer));
    }

    @Test
    void objectToByte() {
        TestObject object1 = new TestObject();
        object1.text = "EXAMPLE";

        byte[] objectBytes = HelperSerializeObject.objectToByte(object1);
        assertEquals(object1, HelperSerializeObject.readObjectFromByteBuffers(ByteBuffer.wrap(objectBytes)));
    }

    @EqualsAndHashCode
    private static class TestObject implements Serializable {
        private static final long serialVersionUID = 1L;

        public String text;
    }
}