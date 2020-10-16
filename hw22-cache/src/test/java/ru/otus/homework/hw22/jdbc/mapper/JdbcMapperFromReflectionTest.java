package ru.otus.homework.hw22.jdbc.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.homework.hw22.core.annotation.Id;
import ru.otus.homework.hw22.jdbc.DbExecutor;

import java.sql.Connection;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcMapperFromReflectionTest {

    @Mock
    private EntityClassMetaDataFromReflection<ClassTest> metaData;
    @Mock
    private EntitySQLMetaDataFromReflection metaSql;
    @Mock
    private DbExecutor<ClassTest> dbExecutor;
    @Mock
    private Connection connection;


    @Test
    void testInsert() throws Exception {
        var jdbcMapper = new JdbcMapperFromReflection<>(metaData, metaSql, dbExecutor);
        jdbcMapper.insert(new ClassTest(), connection);
        verify(metaData).getAllFields();
        verify(metaSql).getInsertSql();
        verify(dbExecutor).executeInsert(eq(connection), any(), any());
        verifyNoMoreInteractions(metaData, metaSql, dbExecutor);
    }

    @Test
    void testUpdate() throws Exception {
        when(metaData.getIdField()).thenReturn(ClassTest.class.getDeclaredField("id"));
        var jdbcMapper = new JdbcMapperFromReflection<>(metaData, metaSql, dbExecutor);
        jdbcMapper.update(new ClassTest(), connection);
        verify(metaData).getFieldsWithoutId();
        verify(metaData).getIdField();
        verify(metaSql).getUpdateSql();
        verify(dbExecutor).executeInsert(eq(connection), any(), any());
        verifyNoMoreInteractions(metaData, metaSql, dbExecutor);
    }

    @Test
    void findById() throws Exception {
        var jdbcMapper = new JdbcMapperFromReflection<>(metaData, metaSql, dbExecutor);
        jdbcMapper.findById(10L, connection);
        verify(metaSql).getSelectByIdSql();
        verify(dbExecutor).executeSelect(eq(connection), any(), eq(10L), any());
        verifyNoMoreInteractions(metaData, metaSql, dbExecutor);
    }

    @Test
    void insertOrUpdate() throws Exception {
        when(metaData.getIdField()).thenReturn(ClassTest.class.getDeclaredField("id"));
        var jdbcMapper = spy(new JdbcMapperFromReflection<>(metaData, metaSql, dbExecutor));
        var classTest = new ClassTest();

        jdbcMapper.insertOrUpdate(classTest, connection);
        verify(jdbcMapper).insert(eq(classTest), eq(connection));

        when(dbExecutor.executeSelect(any(), any(), eq(-1L), any())).thenReturn(Optional.of(classTest));
        jdbcMapper.insertOrUpdate(classTest, connection);
        verify(jdbcMapper).update(eq(classTest), eq(connection));
    }

    private static class ClassTest {

        @Id
        public long id = -1;
        int a = 10;
        String b = "test";
    }
}