package ru.otus.homework.hw18.core.dao;

public class UserDaoException extends RuntimeException {

    public UserDaoException(Exception ex) {
        super(ex);
    }
}
