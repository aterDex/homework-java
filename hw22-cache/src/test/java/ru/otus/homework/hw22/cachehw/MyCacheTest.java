package ru.otus.homework.hw22.cachehw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static ru.otus.homework.hw22.cachehw.MyCache.*;

class MyCacheTest {

    @Test
    void putAndReturnAndRemove() throws Exception {
        var cache = new MyCache<String, String>();
        var key = "key";
        var value = "value";
        cache.put(key, value);

        System.gc();
        Thread.sleep(1000);

        assertEquals(cache.get(key), value);
        cache.remove(key);
        assertNull(cache.get(key));
    }

    @Test
    void putAndNotReturn() throws Exception {
        var cache = new MyCache<String, String>();
        var key = "key";
        var value = "value";
        cache.put(key, value);
        key = null;

        System.gc();
        Thread.sleep(1000);

        assertNull(cache.get(key));
    }

    @Test
    void checkListenerEvent() {
        var cache = new MyCache<String, String>();
        String[] result = {null, null, null};
        cache.addListener((key, value, action) -> {
            result[0] = key;
            result[1] = value;
            result[2] = action;
        });
        String key = "key";
        String value = "value";

        cache.put(key, value);
        assertArrayEquals(new String[]{key, value, ACTION_PUT}, result);

        cache.get(key);
        assertArrayEquals(new String[]{key, value, ACTION_GET}, result);

        cache.get("any another key");
        assertArrayEquals(new String[]{"any another key", null, ACTION_GET}, result);

        cache.remove("any another key");
        assertArrayEquals(new String[]{"any another key", null, ACTION_REMOVE}, result);

        cache.remove(key);
        assertArrayEquals(new String[]{key, value, ACTION_REMOVE}, result);

        cache.remove(key);
        assertArrayEquals(new String[]{key, null, ACTION_REMOVE}, result);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void checkRemoveAndWeakRemoveListener(boolean isWeakClear) throws Exception {
        var cache = new MyCache<String, String>();
        final String[] result = {null, null, null};
        HwListener<String, String> listener = (key, value, action) -> {
            result[0] = key;
            result[1] = value;
            result[2] = action;
        };
        String key = "key";
        String value = "value";
        cache.addListener(listener);
        cache.put(key, value);
        assertArrayEquals(new String[]{key, value, ACTION_PUT}, result);

        result[0] = null;
        result[1] = null;
        result[2] = null;

        if (isWeakClear) {
            listener = null;
            System.gc();
            Thread.sleep(1000);
        } else {
            cache.removeListener(listener);
        }
        cache.put(key, value);
        assertArrayEquals(new String[]{null, null, null}, result);
    }

    @Test
    void checkListenerException() {
        var cache = new MyCache<String, String>();
        cache.addListener((key, value, action) -> {
            throw new RuntimeException();
        });
        cache.put("key", "value");
    }
}