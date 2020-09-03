package ru.otus.processor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ProcessorExceptionIfEvenSecondTest {

    @Test
    void process() {
        long milliseconds = 3242L * 1000 + 234;

        assertThrows(RuntimeException.class, () -> new ProcessorExceptionIfEvenSecond(null, () -> milliseconds).process(null));
        assertThrows(RuntimeException.class, () -> new ProcessorExceptionIfEvenSecond(null, () -> milliseconds + 1).process(null));
        assertThrows(RuntimeException.class, () -> new ProcessorExceptionIfEvenSecond(null, () -> milliseconds + 100).process(null));
        new ProcessorExceptionIfEvenSecond(null, () -> milliseconds + 1000).process(null);
        new ProcessorExceptionIfEvenSecond(null, () -> milliseconds + 1001).process(null);
        new ProcessorExceptionIfEvenSecond(null, () -> null).process(null);
    }
}