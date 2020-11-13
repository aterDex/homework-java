package ru.otus.homework.hw32.common.helper;

import lombok.SneakyThrows;

import java.io.*;
import java.nio.ByteBuffer;

public class HelperSerializeObject {

    private HelperSerializeObject() {
    }

    @SneakyThrows
    public static Object readObjectFromByteBuffers(ByteBuffer buffer) {
        try (InputStream is = new InputStreamByByteBuffer(buffer);
             ObjectInputStream oos = new ObjectInputStream(is)) {
            return oos.readObject();
        }
    }

    @SneakyThrows
    public static byte[] objectToByte(Serializable serial) {
        try (var arrayOutputStream = new ByteArrayOutputStream();
             var outputStream = new ObjectOutputStream(arrayOutputStream)) {
            outputStream.writeObject(serial);
            return arrayOutputStream.toByteArray();
        }
    }
}
