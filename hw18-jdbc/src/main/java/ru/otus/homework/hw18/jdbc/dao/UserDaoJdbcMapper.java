package ru.otus.homework.hw18.jdbc.dao;

import lombok.SneakyThrows;
import ru.otus.homework.hw18.core.dao.UserDao;
import ru.otus.homework.hw18.core.model.User;
import ru.otus.homework.hw18.core.sessionmanager.SessionManager;
import ru.otus.homework.hw18.jdbc.mapper.JdbcMapper;
import ru.otus.homework.hw18.jdbc.sessionmanager.SessionManagerJdbc;

import java.util.Optional;

public class UserDaoJdbcMapper implements UserDao {

    private final JdbcMapper<User> mapper;
    private final SessionManagerJdbc sessionManager;

    public UserDaoJdbcMapper(JdbcMapper<User> mapper, SessionManagerJdbc sessionManager) {
        this.mapper = mapper;
        this.sessionManager = sessionManager;
    }

    @Override
    public Optional<User> findById(long id) {
        return mapper.findById(id, sessionManager.getCurrentSession().getConnection());
    }

    @Override
    @SneakyThrows
    public long insertUser(User user) {
        return mapper.insert(user, sessionManager.getCurrentSession().getConnection());
    }

    @Override
    public void updateUser(User user) {
        mapper.update(user, sessionManager.getCurrentSession().getConnection());
    }

    @Override
    public void insertOrUpdate(User user) {
        mapper.insertOrUpdate(user, sessionManager.getCurrentSession().getConnection());
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
