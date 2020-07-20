package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestClass;
import ru.otus.homework.test.TestClassPreparer;
import ru.otus.homework.test.TestClassPreparerException;
import ru.otus.homework.test.api.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TestClassPreparerByAnnotation implements TestClassPreparer {

    private static final String ERROR_TEXT_TWO_ANNOTATION = "В классе теста не должно быть больше одного метод с аннотацией";
    private static final String ERROR_TEXT_EMPTY = "В классе теста не найдены тесты";

    @Override
    public TestClass prepare(Class clazz) throws TestClassPreparerException {
        Method[] methods = clazz.getDeclaredMethods();
        TestClassFromMethodsBuilder builderTestClass = new TestClassFromMethodsBuilder(clazz);
        for (Method method : methods) {
            prepare(method, builderTestClass);
        }
        if (builderTestClass.getTestMethodCount() == 0) {
            throw new TestClassPreparerException(ERROR_TEXT_EMPTY, clazz);
        }
        return builderTestClass.createTestClass();
    }


    private void prepare(Method method, TestClassFromMethodsBuilder builder) {
        if (method.getDeclaredAnnotation(Test.class) != null) {
            builder.addTestMethod("", method);
        } else if (method.getDeclaredAnnotation(After.class) != null) {
            addMethods(method, builder::getAfter, builder::setAfter, After.class, builder);
        } else if (method.getDeclaredAnnotation(Before.class) != null) {
            addMethods(method, builder::getBefore, builder::setBefore, Before.class, builder);
        } else if (method.getDeclaredAnnotation(AfterAll.class) != null) {
            addMethods(method, builder::getAfterAll, builder::setAfterAll, AfterAll.class, builder);
        } else if (method.getDeclaredAnnotation(BeforeAll.class) != null) {
            addMethods(method, builder::getBeforeAll, builder::setBeforeAll, BeforeAll.class, builder);
        }
    }

    private void addMethods(Method method, Supplier<Method> supplier, Consumer<Method> consumer, Class<? extends Annotation> annotation, TestClassFromMethodsBuilder builder) {
        if (supplier.get() != null) {
            throw new TestClassPreparerException(ERROR_TEXT_TWO_ANNOTATION + " " + annotation.getName(), builder.getClazz());
        }
        consumer.accept(method);
    }
}
