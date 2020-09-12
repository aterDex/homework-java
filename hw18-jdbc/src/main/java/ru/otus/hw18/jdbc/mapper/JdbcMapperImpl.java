package ru.otus.hw18.jdbc.mapper;

import lombok.SneakyThrows;
import ru.otus.hw18.jdbc.DbExecutor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сохратяет объект в базу, читает объект из базы
 *
 * @param <T>
 */
public class JdbcMapperImpl<T> implements JdbcMapper<T> {

    private final EntityClassMetaData<T> entityClassMetaData;
    private final EntitySQLMetaData entitySQLMetaData;
    private final DbExecutor<T> dbExecutor;

    public JdbcMapperImpl(EntityClassMetaData<T> entityClassMetaData, EntitySQLMetaData entitySQLMetaData, DbExecutor<T> dbExecutor) {
        this.entityClassMetaData = entityClassMetaData;
        this.entitySQLMetaData = entitySQLMetaData;
        this.dbExecutor = dbExecutor;
    }

    @Override
    @SneakyThrows
    public long insert(T objectData, Connection connection) {
        List<Object> listParam = entityClassMetaData.getAllFields().stream().map(x -> extractField(x, objectData)).collect(Collectors.toList());
        return dbExecutor.executeInsert(connection, entitySQLMetaData.getInsertSql(), listParam);
    }

    @Override
    @SneakyThrows
    public void update(T objectData, Connection connection) {
        List<Object> listParam = Stream.concat(entityClassMetaData.getFieldsWithoutId().stream(), Stream.of(entityClassMetaData.getIdField()))
                .map(x -> extractField(x, objectData))
                .collect(Collectors.toList());
        dbExecutor.executeInsert(connection, entitySQLMetaData.getUpdateSql(), listParam);
    }

    @Override
    @SneakyThrows
    public void insertOrUpdate(T objectData, Connection connection) {
        Object id = entityClassMetaData.getIdField().get(objectData);
        if (findById(id, connection).isPresent()) {
            update(objectData, connection);
        } else {
            insert(objectData, connection);
        }
    }

    @Override
    @SneakyThrows
    public Optional<T> findById(Object id, Connection connection) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), id, this::mapper);
    }

    @SneakyThrows
    private T mapper(ResultSet resultSet) {
        if (resultSet.next()) {
            var constructor = entityClassMetaData.getConstructor();
            T inst = constructor.newInstance();
            for (Field allField : entityClassMetaData.getAllFields()) {
                String name = allField.getName();
                allField.set(inst, resultSet.getObject(name));
            }
            return inst;
        }
        return null;
    }

    @SneakyThrows
    private Object extractField(Field field, Object obj) {
        return field.get(obj);
    }
}
