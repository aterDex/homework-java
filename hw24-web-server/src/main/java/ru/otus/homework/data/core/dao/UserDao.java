package ru.otus.homework.data.core.dao;

import ru.otus.homework.data.core.model.User;
import ru.otus.homework.data.core.sessionmanager.SessionManager;

import java.util.Optional;

public interface UserDao {
    Optional<User> findById(long id);

    long insertUser(User user);

    void updateUser(User user);

    void insertOrUpdate(User user);

    SessionManager getSessionManager();

    Optional<User> findByLogin(String login);
}
