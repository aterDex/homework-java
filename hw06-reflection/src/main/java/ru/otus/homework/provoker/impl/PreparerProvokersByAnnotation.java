package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.api.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PreparerProvokersByAnnotation implements PreparerProvokers {

    protected static final Collection<Class<? extends Annotation>> NOT_MIX_ANNOTATION = List.of(Test.class, After.class, Before.class, AfterAll.class, BeforeAll.class);

    @Override
    public ProvokerClass prepare(Class<?> clazz) throws PreparerProvokersException {
        Method[] methods = clazz.getDeclaredMethods();
        ProvokerClassFromMethodsBuilder builderProvokerClass = new ProvokerClassFromMethodsBuilder(clazz);
        for (Method method : methods) {
            prepare(method, builderProvokerClass);
        }
        if (builderProvokerClass.getTestMethodCount() == 0) {
            throw new PreparerProvokersException("В классе теста не найдены тесты", clazz);
        }
        Description classDescription = clazz.getDeclaredAnnotation(Description.class);
        builderProvokerClass.setDescription(classDescription == null || classDescription.value() == null || classDescription.value().isBlank() ? "" : classDescription.value());
        return builderProvokerClass.createTestClass();
    }

    private void prepare(Method method, ProvokerClassFromMethodsBuilder builderProvokerClass) {
        isMixAnnotation(method, builderProvokerClass);
        if (method.getDeclaredAnnotation(Test.class) != null) {
            isNotStatic(method, Test.class, builderProvokerClass);
            isEmptyArgAndResult(method, Test.class, builderProvokerClass);
            addMethods(method, null, method1 -> builderProvokerClass.addTestMethod(getDescription(method), method1), Test.class, builderProvokerClass);
        } else if (method.getDeclaredAnnotation(After.class) != null) {
            isNotStatic(method, After.class, builderProvokerClass);
            isEmptyArgAndResult(method, After.class, builderProvokerClass);
            addMethods(method, builderProvokerClass::getAfter, builderProvokerClass::setAfter, After.class, builderProvokerClass);
        } else if (method.getDeclaredAnnotation(Before.class) != null) {
            isNotStatic(method, Before.class, builderProvokerClass);
            isEmptyArgAndResult(method, Before.class, builderProvokerClass);
            addMethods(method, builderProvokerClass::getBefore, builderProvokerClass::setBefore, Before.class, builderProvokerClass);
        } else if (method.getDeclaredAnnotation(AfterAll.class) != null) {
            isStatic(method, AfterAll.class, builderProvokerClass);
            isEmptyArgAndResult(method, AfterAll.class, builderProvokerClass);
            addMethods(method, builderProvokerClass::getAfterAll, builderProvokerClass::setAfterAll, AfterAll.class, builderProvokerClass);
        } else if (method.getDeclaredAnnotation(BeforeAll.class) != null) {
            isStatic(method, BeforeAll.class, builderProvokerClass);
            isEmptyArgAndResult(method, BeforeAll.class, builderProvokerClass);
            addMethods(method, builderProvokerClass::getBeforeAll, builderProvokerClass::setBeforeAll, BeforeAll.class, builderProvokerClass);
        }
    }

    private String getDescription(Method method) {
        Description description = method.getDeclaredAnnotation(Description.class);
        return description == null || description.value() == null || description.value().isBlank() ? "" : description.value();
    }

    private void isMixAnnotation(Method method, ProvokerClassFromMethodsBuilder builder) {
        if (NOT_MIX_ANNOTATION.stream().filter(x -> method.getDeclaredAnnotation(x) != null).count() > 1) {
            throw new PreparerProvokersException(String.format("Метод %s не может быть принят, не смогли определить поведение метода, не совместимы аннотации.", method.getName()), builder.getClazz());
        }
    }

    private void isNotStatic(Method method, Class<? extends Annotation> annotation, ProvokerClassFromMethodsBuilder builder) {
        if (Modifier.isStatic(method.getModifiers())) {
            throw new PreparerProvokersException(String.format("Метод %s помеченный аннотацией %s не может быть статичным", method.getName(), annotation.getName()), builder.getClazz());
        }
    }

    private void isStatic(Method method, Class<? extends Annotation> annotation, ProvokerClassFromMethodsBuilder builder) {
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new PreparerProvokersException(String.format("Метод %s помеченный аннотацией %s должен быть статичным", method.getName(), annotation.getName()), builder.getClazz());
        }
    }

    private void isEmptyArgAndResult(Method method, Class<? extends Annotation> annotation, ProvokerClassFromMethodsBuilder builder) {
        if (method.getParameterCount() > 0) {
            throw new PreparerProvokersException(String.format("Метод %s помеченный аннотацией %s не должен принимать параметры", method.getName(), annotation.getName(), builder.getClazz().getCanonicalName()), builder.getClazz());
        }
        if (!method.getReturnType().equals(Void.TYPE)) {
            throw new PreparerProvokersException(String.format("Метод %s помеченный аннотацией %s не должен возвращать что либо", method.getName(), annotation.getName(), builder.getClazz().getCanonicalName()), builder.getClazz());
        }
    }

    private void addMethods(Method method, Supplier<Method> supplier, Consumer<Method> consumer, Class<? extends Annotation> annotation, ProvokerClassFromMethodsBuilder builder) {
        if (supplier != null && supplier.get() != null) {
            throw new PreparerProvokersException("В классе теста не должно быть больше одного метод с аннотацией " + annotation.getName(), builder.getClazz());
        }
        method.setAccessible(true);
        consumer.accept(method);
    }
}
