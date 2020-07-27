package ru.otus.homework.provoker.impl;

import org.junit.jupiter.api.Test;
import ru.otus.homework.provoker.impl.loader.CheckLoader0;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterForScanPackageByPrefixTest {

    @Test
    void test1() {
        FilterForScanPackageByPrefix filter = new FilterForScanPackageByPrefix("Check");
        assertTrue(filter.test(CheckLoader0.class));
        assertFalse(filter.test(ArrayList.class));
    }

    @Test
    void test2() {
        FilterForScanPackageByPrefix filter = new FilterForScanPackageByPrefix();
        assertTrue(filter.test(CheckLoader0.class));
        assertTrue(filter.test(ArrayList.class));
    }
}