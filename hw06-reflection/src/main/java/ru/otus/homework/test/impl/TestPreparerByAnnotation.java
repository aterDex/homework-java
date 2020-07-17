package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestPrepared;
import ru.otus.homework.test.TestPreparer;
import ru.otus.homework.test.api.*;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Подготавливаем тесты на основе аннотаций
 */
public class TestPreparerByAnnotation implements TestPreparer {

    private static final String ERROR_TEXT_TWO_ANNOTATION = "В классе теста не должно быть больше одного метод с аннотацией";

    @Override
    public TestPrepared prepare(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        TestPreparedImmutable.TestPrepareBuilder builderPrepare = TestPreparedImmutable.builder()
                .clazz(clazz)
                .tests(new ArrayList<>(methods.length));
        for (Method method : methods) {
            String problem = prepare(method, builderPrepare);
            if (problem != null) {
                return builderPrepare.buildNotValid(problem);
            }
        }
        return builderPrepare.getCountTests() == 0 ? builderPrepare.buildNotValid("В классе нет тестов.") : builderPrepare.build();
    }

    private String prepare(Method method, TestPreparedImmutable.TestPrepareBuilder builderPrepare) {
        if (method.getDeclaredAnnotation(Test.class) != null) {
            builderPrepare.addTest(method);
        } else if (method.getDeclaredAnnotation(After.class) != null) {
            if (builderPrepare.isWasAfter()) {
                return ERROR_TEXT_TWO_ANNOTATION + " After";
            }
            builderPrepare.after(method);
        } else if (method.getDeclaredAnnotation(Before.class) != null) {
            if (builderPrepare.isWasBefore()) {
                return ERROR_TEXT_TWO_ANNOTATION + " Before";
            }
            builderPrepare.before(method);
        } else if (method.getDeclaredAnnotation(AfterAll.class) != null) {
            if (builderPrepare.isWasAfterAll()) {
                return ERROR_TEXT_TWO_ANNOTATION + " AfterAll";
            }
            builderPrepare.afterAll(method);
        } else if (method.getDeclaredAnnotation(BeforeAll.class) != null) {
            if (builderPrepare.isWasBeforeAll()) {
                return ERROR_TEXT_TWO_ANNOTATION + " BeforeAll";
            }
            builderPrepare.beforeAll(method);
        }
        return null;
    }
}
