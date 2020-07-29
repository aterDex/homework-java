package ru.otus.homework.provoker.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.homework.provoker.impl.loader.CheckLoader0;
import ru.otus.homework.provoker.impl.loader.inner.CheckLoader1;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DetectiveScanPackageTest {

    @Test
    void searchWithoutFilter() {
        DetectiveScanPackageWithFilter scanPackage = new DetectiveScanPackageWithFilter("ru.otus.homework.provoker.impl.loader", null, DetectiveScanPackageTest.class.getClassLoader());
        Set<Class<?>> classesCheck = new HashSet<>(Arrays.asList(CheckLoader0.class, CheckLoader1.class));
        Collection<Class<?>> result = scanPackage.search();
        assertEquals(2, result.size());
        for (Class<?> aClass : result) {
            assertTrue(classesCheck.remove(aClass));
        }
        assertTrue(classesCheck.isEmpty());
    }

    @Test
    void searchWithFilter() {
        Predicate filter = Mockito.mock(Predicate.class);
        Mockito.when(filter.test(Mockito.any())).thenReturn(false);
        Mockito.when(filter.test(CheckLoader1.class)).thenReturn(true);

        DetectiveScanPackageWithFilter scanPackage = new DetectiveScanPackageWithFilter("ru.otus.homework.provoker.impl.loader", filter, DetectiveScanPackageTest.class.getClassLoader());
        Collection<Class<?>> result = scanPackage.search();
        assertEquals(1, result.size());
        assertEquals(CheckLoader1.class, result.iterator().next());
    }
}