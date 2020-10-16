package ru.otus.homework.hw22.cachehw;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HwListenerLogger<K, V> implements HwListener<K, V> {
    @Override
    public void notify(K key, V value, HwListenerAction action) {
        log.info("Cache notify -> Action: [{}] Key: [{}] Value: [{}]", action, key, value);
    }
}
