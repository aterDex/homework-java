package ru.otus.homework.hw22.core.service;

import ru.otus.homework.hw22.core.model.User;

import java.util.Optional;

public interface DBServiceUser {

    long saveUser(User user);

    Optional<User> getUser(long id);
}
