package ru.otus.homework.hw22.cachehw;

import lombok.extern.slf4j.Slf4j;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.WeakHashMap;

@Slf4j
public class MyCache<K, V> implements HwCache<K, V> {

    private final WeakHashMap<K, V> cache = new WeakHashMap<>();
    private final Collection<WeakReference<HwListener<K, V>>> listeners = new LinkedList<>();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        notify(key, value, HwListenerAction.PUT);
    }

    @Override
    public void remove(K key) {
        V value = cache.remove(key);
        notify(key, value, HwListenerAction.REMOVE);
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        notify(key, value, HwListenerAction.GET);
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        var iter = listeners.iterator();
        while (iter.hasNext()) {
            var listenerCheck = iter.next().get();
            if (listenerCheck == null || listenerCheck.equals(listener)) {
                iter.remove();
            }
        }
    }

    private void notify(K key, V value, HwListenerAction action) {
        var iter = listeners.iterator();
        while (iter.hasNext()) {
            try {
                var listener = iter.next().get();
                if (listener != null) {
                    listener.notify(key, value, action);
                } else {
                    iter.remove();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
