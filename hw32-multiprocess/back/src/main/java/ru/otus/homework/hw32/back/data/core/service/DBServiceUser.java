package ru.otus.homework.hw32.back.data.core.service;


import ru.otus.homework.hw32.back.data.core.model.User;

import java.util.List;
import java.util.Optional;

public interface DBServiceUser {

    long saveUser(User user);

    Optional<User> getUser(long id);

    List<User> getUsers();

    Optional<User> findByLogin(String login);
}
