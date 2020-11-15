package ru.otus.homework.hw32.back.data.core.sessionmanager;


public class SessionManagerException extends RuntimeException {
    public SessionManagerException(String msg) {
        super(msg);
    }

    public SessionManagerException(Exception ex) {
        super(ex);
    }
}
