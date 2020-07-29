package ru.otus.homework.provoker.impl;

import java.util.function.Predicate;

public class FilterForScanPackageByPrefix implements Predicate<Class<?>> {

    private String prefix = null;

    public FilterForScanPackageByPrefix(String prefix) {
        this.prefix = prefix;
    }

    public FilterForScanPackageByPrefix() {
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean test(Class<?> clazz) {
        return prefix == null || clazz.getSimpleName().startsWith(prefix);
    }
}
