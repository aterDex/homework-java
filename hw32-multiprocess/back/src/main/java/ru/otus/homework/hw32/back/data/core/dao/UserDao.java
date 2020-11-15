package ru.otus.homework.hw32.back.data.core.dao;


import ru.otus.homework.hw32.back.data.core.model.User;
import ru.otus.homework.hw32.back.data.core.sessionmanager.SessionManager;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(long id);

    long insertUser(User user);

    void updateUser(User user);

    void insertOrUpdate(User user);

    SessionManager getSessionManager();

    Optional<User> findByLogin(String login);

    List<User> getUsers();
}
