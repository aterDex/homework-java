package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.api.ProvokerClassMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ProvokerClassFromMethodsBuilder {

    private final Class<?> clazz;
    private String description;
    private Method beforeAll;
    private Method afterAll;
    private Method before;
    private Method after;
    private final List<Method> methods = new ArrayList<>();
    private final List<String> methodsDescription = new ArrayList<>();

    public ProvokerClassFromMethodsBuilder(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Method getBeforeAll() {
        return beforeAll;
    }

    public void setBeforeAll(Method beforeAll) {
        this.beforeAll = beforeAll;
    }

    public Method getAfterAll() {
        return afterAll;
    }

    public void setAfterAll(Method afterAll) {
        this.afterAll = afterAll;
    }

    public Method getBefore() {
        return before;
    }

    public void setBefore(Method before) {
        this.before = before;
    }

    public Method getAfter() {
        return after;
    }

    public void setAfter(Method after) {
        this.after = after;
    }

    public void addTestMethod(String description, Method test) {
        methodsDescription.add(description);
        methods.add(test);
    }

    public ProvokerClassFromReflectMethods createTestClass() {
        return new ProvokerClassFromReflectMethods(clazz, description, beforeAll, afterAll, createTestMethodsClass());
    }

    public int getTestMethodCount() {
        return methods.size();
    }

    private List<ProvokerClassMethod> createTestMethodsClass() {
        List<ProvokerClassMethod> provokerClassMethods = new ArrayList<>(methods.size());
        for (int i = 0; i < methods.size(); i++) {
            provokerClassMethods.add(new ProvokerClassMethodFromReflectMethods(clazz, methodsDescription.get(i), before, methods.get(i), after));
        }
        return provokerClassMethods;
    }
}
