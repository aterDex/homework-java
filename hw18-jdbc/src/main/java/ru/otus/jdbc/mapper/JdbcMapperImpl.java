package ru.otus.jdbc.mapper;

import lombok.SneakyThrows;
import ru.otus.h2.DataSourceH2;
import ru.otus.jdbc.DbExecutor;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сохратяет объект в базу, читает объект из базы
 * @param <T>
 */
public class JdbcMapperImpl<T> implements JdbcMapper<T> {

    private final EntityClassMetaData<T> entityClassMetaData;
    private final EntitySQLMetaData entitySQLMetaData;
    private final DbExecutor<T> dbExecutor;
    private final DataSource dataSource;

    public JdbcMapperImpl(EntityClassMetaData<T> entityClassMetaData, EntitySQLMetaData entitySQLMetaData, DbExecutor<T> dbExecutor, DataSource dataSource) {
        this.entityClassMetaData = entityClassMetaData;
        this.entitySQLMetaData = entitySQLMetaData;
        this.dbExecutor = dbExecutor;
        this.dataSource = dataSource;
    }

    @Override
    @SneakyThrows
    public void insert(T objectData) {
        try (Connection connection = dataSource.getConnection()) {
            List<Object> listParam = entityClassMetaData.getAllFields().stream().map(x -> get(x, objectData)).collect(Collectors.toList());
            dbExecutor.executeInsert(connection, entitySQLMetaData.getInsertSql(), listParam);
        }
    }

    @Override
    public void update(T objectData) {
    }

    @Override
    public void insertOrUpdate(T objectData) {
    }

    @Override
    public T findById(Object id, Class<T> clazz) {
        return null;
    }

    @SneakyThrows
    private Object get(Field field, Object obj) {
        return field.get(obj);
    }
}
