package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.api.ProvokerClass;
import ru.otus.homework.provoker.api.PreparerProvokers;
import ru.otus.homework.provoker.api.PreparerProvokersException;
import ru.otus.homework.provoker.api.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PreparerProvokersByAnnotation implements PreparerProvokers {

    private static final String ERROR_TEXT_TWO_ANNOTATION = "В классе теста не должно быть больше одного метод с аннотацией";
    private static final String ERROR_TEXT_EMPTY = "В классе теста не найдены тесты";

    @Override
    public ProvokerClass prepare(Class clazz) throws PreparerProvokersException {
        Method[] methods = clazz.getDeclaredMethods();
        ProvokerClassFromMethodsBuilder builderProvokerClass = new ProvokerClassFromMethodsBuilder(clazz);
        for (Method method : methods) {
            prepare(method, builderProvokerClass);
        }
        if (builderProvokerClass.getTestMethodCount() == 0) {
            throw new PreparerProvokersException(ERROR_TEXT_EMPTY, clazz);
        }
        return builderProvokerClass.createTestClass();
    }


    private void prepare(Method method, ProvokerClassFromMethodsBuilder builderProvokerClass) {
        if (method.getDeclaredAnnotation(Test.class) != null) {
            builderProvokerClass.addTestMethod("", method);
        } else if (method.getDeclaredAnnotation(After.class) != null) {
            addMethods(method, builderProvokerClass::getAfter, builderProvokerClass::setAfter, After.class, builderProvokerClass);
        } else if (method.getDeclaredAnnotation(Before.class) != null) {
            addMethods(method, builderProvokerClass::getBefore, builderProvokerClass::setBefore, Before.class, builderProvokerClass);
        } else if (method.getDeclaredAnnotation(AfterAll.class) != null) {
            addMethods(method, builderProvokerClass::getAfterAll, builderProvokerClass::setAfterAll, AfterAll.class, builderProvokerClass);
        } else if (method.getDeclaredAnnotation(BeforeAll.class) != null) {
            addMethods(method, builderProvokerClass::getBeforeAll, builderProvokerClass::setBeforeAll, BeforeAll.class, builderProvokerClass);
        }
    }

    private void addMethods(Method method, Supplier<Method> supplier, Consumer<Method> consumer, Class<? extends Annotation> annotation, ProvokerClassFromMethodsBuilder builder) {
        if (supplier.get() != null) {
            throw new PreparerProvokersException(ERROR_TEXT_TWO_ANNOTATION + " " + annotation.getName(), builder.getClazz());
        }
        consumer.accept(method);
    }
}
