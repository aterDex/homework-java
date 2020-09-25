package ru.otus.homework.hw22.cachehw;

import lombok.extern.slf4j.Slf4j;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.function.Function;

@Slf4j
public class MyCache<K, V> implements HwCache<K, V> {

    private final Function<K, String> keyNormalizer;
    private final WeakHashMap<String, V> cache = new WeakHashMap<>();
    private final Collection<WeakReference<HwListener>> listeners = new LinkedList<>();

    public MyCache() {
        keyNormalizer = x -> String.valueOf(x.hashCode());
    }

    public MyCache(Function<K, String> keyNormalizer) {
        if (keyNormalizer == null) {
            throw new IllegalArgumentException("keyNormalizer mustn't be null.");
        }
        this.keyNormalizer = keyNormalizer;
    }

    @Override
    public void put(K key, V value) {
        cache.put(keyNormalizer.apply(key), value);
        notify(key, value, "put");
    }

    @Override
    public void remove(K key) {
        V value = cache.remove(keyNormalizer.apply(key));
        notify(key, value, "remove");
    }

    @Override
    public V get(K key) {
        V value = cache.get(keyNormalizer.apply(key));
        notify(key, value, "get");
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        Iterator<WeakReference<HwListener>> iter = listeners.iterator();
        while (iter.hasNext()) {
            HwListener listenerCheck = iter.next().get();
            if (listenerCheck == null || listenerCheck.equals(listener)) {
                iter.remove();
            }
        }
    }

    private void notify(K key, V value, String action) {
        Iterator<WeakReference<HwListener>> iter = listeners.iterator();
        while (iter.hasNext()) {
            try {
                HwListener listener = iter.next().get();
                if (listener != null) {
                    listener.notify(key, value, action);
                } else {
                    iter.remove();
                }
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }
}
