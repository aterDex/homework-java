package ru.otus.homework.hw22.jdbc.mapper;

import org.junit.jupiter.api.Test;
import ru.otus.homework.hw22.core.annotation.Id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityClassMetaDataFromReflectionTest {

    @Test
    void testClassWithoutId() {
        assertThrows(RuntimeException.class, () -> new EntityClassMetaDataFromReflection<>(ClassWithoutId.class));
    }

    @Test
    void testClassTwoId() {
        assertThrows(RuntimeException.class, () -> new EntityClassMetaDataFromReflection<>(ClassTwoId.class));
    }

    @Test
    void testOneId() {
        var meta = new EntityClassMetaDataFromReflection<>(ClassOneId.class);
        assertEquals("b2", meta.getIdField().getName());
        assertEquals(1, meta.getFieldsWithoutId().size());
        assertEquals("a1", meta.getFieldsWithoutId().get(0).getName());
    }

    @Test
    void testClassOnlyArgConstructor() {
        assertThrows(NoSuchMethodException.class, () -> new EntityClassMetaDataFromReflection<>(ClassOnlyArgConstructor.class));
    }

    private static class ClassWithoutId {
        int a1;
        int a2;
    }

    private static class ClassTwoId {
        int a1;
        int a2;

        @Id
        int id1;
        @Id
        int id2;
    }

    private static class ClassOneId {
        int a1;
        @Id
        int b2;
    }

    private static class ClassOnlyArgConstructor {
        int a1;
        @Id
        int b2;
        ClassOnlyArgConstructor(int a) {
        }
    }
}