package ru.otus.hw18.core.dao;

import java.util.Optional;

import ru.otus.hw18.core.model.User;
import ru.otus.hw18.core.sessionmanager.SessionManager;

public interface UserDao {

    Optional<User> findById(long id);
    long insertUser(User user);
    void updateUser(User user);
    void insertOrUpdate(User user);
    SessionManager getSessionManager();
}
