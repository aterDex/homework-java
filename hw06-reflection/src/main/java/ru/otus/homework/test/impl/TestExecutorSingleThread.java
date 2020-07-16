package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestExecutor;
import ru.otus.homework.test.TestResult;
import ru.otus.homework.test.api.AfterAll;
import ru.otus.homework.test.api.BeforeAll;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class TestExecutorSingleThread implements TestExecutor<TestResult> {

    @Override
    public Collection<TestResult> execute(Class aClass) {
            executeStatic(findStaticMethod(aClass, BeforeAll.class));
        try {
            executeTests(aClass);
        } finally {
            executeStatic(findStaticMethod(aClass, AfterAll.class));
        }
        return null;
    }

    private void executeStatic(Optional<Method> staticMethod) throws InvocationTargetException, IllegalAccessException {
        if (staticMethod.isEmpty()) return;
        Method method = staticMethod.get();
        if (Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 0) {
            method.invoke(null);
        }
    }

    private Optional<Method> findStaticMethod(Class aClass, Class<? extends Annotation> annotation) {
        return Arrays.stream(aClass.getMethods())
                .filter(x -> x.isAnnotationPresent(annotation)).findFirst();
    }
}