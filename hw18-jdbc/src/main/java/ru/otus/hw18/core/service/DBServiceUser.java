package ru.otus.hw18.core.service;

import ru.otus.hw18.core.model.User;

import java.util.Optional;

public interface DBServiceUser {

    long saveUser(User user);

    Optional<User> getUser(long id);
}
