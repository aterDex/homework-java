package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestPrepared;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Информация по обработанному тесту
 */
public final class TestPreparedImmutable implements TestPrepared {

    /**
     * Класс теста
     */
    private final Class clazz;
    /**
     * Метод который выполняем до всего тесту
     */
    private final Method beforeAll;
    /**
     * Метод который выполняется после всех тесту
     */
    private final Method afterAll;
    /**
     * Метод выполняем до каждого тесту
     */
    private final Method before;
    /**
     * Метод выполняем после каждого тесту
     */
    private final Method after;
    /**
     * Тесты которые нужно выполнить
     */
    private final Collection<Method> tests;

    /**
     * Тест обработан и может быть выполнен
     */
    private final boolean valid;

    /**
     * Описания проблем теста
     */
    private final String problemsDescription;

    TestPreparedImmutable(Class clazz, Method beforeAll, Method afterAll, Method before, Method after, Collection<Method> tests) {
        this.clazz = clazz;
        this.beforeAll = beforeAll;
        this.afterAll = afterAll;
        this.before = before;
        this.after = after;
        this.tests = tests;
        this.valid = true;
        this.problemsDescription = null;
    }

    TestPreparedImmutable(Class clazz, String problemsDescription) {
        this.clazz = clazz;
        this.beforeAll = null;
        this.afterAll = null;
        this.before = null;
        this.after = null;
        this.tests = null;
        this.valid = false;
        this.problemsDescription = problemsDescription;
    }

    public static TestPrepareBuilder builder() {
        return new TestPrepareBuilder();
    }

    @Override
    public Class getClazz() {
        return this.clazz;
    }

    @Override
    public Method getBeforeAll() {
        return this.beforeAll;
    }

    @Override
    public Method getAfterAll() {
        return this.afterAll;
    }

    @Override
    public Method getBefore() {
        return this.before;
    }

    @Override
    public Method getAfter() {
        return this.after;
    }

    @Override
    public Collection<Method> getTests() {
        return this.tests;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Override
    public String getProblemsDescription() {
        return this.problemsDescription;
    }

    public static class TestPrepareBuilder {
        private Class clazz;
        private Method beforeAll;
        private Method afterAll;
        private Method before;
        private Method after;
        private List<Method> tests;

        public TestPrepareBuilder clazz(Class clazz) {
            this.clazz = clazz;
            return this;
        }

        public TestPrepareBuilder beforeAll(Method beforeAll) {
            this.beforeAll = beforeAll;
            return this;
        }

        public TestPrepareBuilder afterAll(Method afterAll) {
            this.afterAll = afterAll;
            return this;
        }

        public TestPrepareBuilder before(Method before) {
            this.before = before;
            return this;
        }

        public TestPrepareBuilder after(Method after) {
            this.after = after;
            return this;
        }

        public TestPrepareBuilder tests(List<Method> tests) {
            this.tests = tests;
            return this;
        }

        public TestPrepareBuilder addTest(Method method) {
            this.tests.add(method);
            return this;
        }

        public TestPrepared build() {
            return new TestPreparedImmutable(clazz, beforeAll, afterAll, before, after, Collections.unmodifiableCollection(new ArrayList<>(tests)));
        }

        public TestPrepared buildNotValid(String problemsDescription) {
            return new TestPreparedImmutable(clazz, problemsDescription);
        }

        public boolean isWasAfter() {
            return after != null;
        }

        public boolean isWasBefore() {
            return before != null;
        }

        public boolean isWasAfterAll() {
            return afterAll != null;
        }

        public boolean isWasBeforeAll() {
            return beforeAll != null;
        }

        public int getCountTests() {
            return tests == null ? 0 : tests.size();
        }
    }
}
