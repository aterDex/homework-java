package ru.otus.homework.hw18.jdbc.mapper;

import org.junit.jupiter.api.Test;
import ru.otus.homework.hw18.core.annotation.Id;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntitySQLMetaDataFromReflectionTest {

    @Test
    void test() {
        var sql = new EntitySQLMetaDataFromReflection(new EntityClassMetaDataFromReflection<>(ClassTest.class));
        assertEquals("insert into ClassTest (a, b, c, TTT) values (?, ?, ?, ?)", sql.getInsertSql());
        assertEquals("select a, b, c, TTT from ClassTest", sql.getSelectAllSql());
        assertEquals("select a, b, c, TTT from ClassTest where TTT = ?", sql.getSelectByIdSql());
        assertEquals("update ClassTest set a = ?, b = ?, c = ? where TTT = ?", sql.getUpdateSql());
    }

    private static class ClassTest {

        int a;
        String b;
        Object c;

        @Id
        long TTT;
    }
}