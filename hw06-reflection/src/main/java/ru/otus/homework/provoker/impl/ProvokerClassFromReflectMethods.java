package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.ProvokerClass;
import ru.otus.homework.provoker.ProvokerClassMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ProvokerClassFromReflectMethods implements ProvokerClass {

    private final Class clazz;
    private final String description;
    private final Method beforeAll;
    private final Method afterAll;
    private final List<ProvokerClassMethod> provokerClassMethods;

    public ProvokerClassFromReflectMethods(Class clazz, String description, Method beforeAll, Method afterAll, List<? extends ProvokerClassMethod> testClassMethods) {
        this.clazz = clazz;
        this.description = description;
        this.beforeAll = beforeAll;
        this.afterAll = afterAll;
        this.provokerClassMethods = Collections.unmodifiableList(new ArrayList<>(testClassMethods));
    }

    @Override
    public Class getClazz() {
        return clazz;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Optional<Throwable> beforeAll() {
        return InvokeUtil.invokeMethod(beforeAll, null);
    }

    @Override
    public Optional<Throwable> afterAll() {
        return InvokeUtil.invokeMethod(afterAll, null);
    }

    @Override
    public List<ProvokerClassMethod> getTestMethods() {
        return provokerClassMethods;
    }

    @Override
    public boolean isBeforeAll() {
        return beforeAll != null;
    }

    @Override
    public boolean isAfterAll() {
        return afterAll != null;
    }
}
