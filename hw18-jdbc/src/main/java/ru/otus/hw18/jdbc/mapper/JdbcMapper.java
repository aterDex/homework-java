package ru.otus.hw18.jdbc.mapper;

import java.sql.Connection;
import java.util.Optional;

/**
 * Сохратяет объект в базу, читает объект из базы
 * @param <T>
 */
public interface JdbcMapper<T> {
    long insert(T objectData, Connection connection);

    void update(T objectData, Connection connection);

    void insertOrUpdate(T objectData, Connection connection);

    Optional<T> findById(Object id, Connection connection);
}
