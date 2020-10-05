package ru.otus.homework.hw25.appcontainer.api;

public class AppComponentsContainerException extends RuntimeException {

    public AppComponentsContainerException() {
    }

    public AppComponentsContainerException(String message) {
        super(message);
    }

    public AppComponentsContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppComponentsContainerException(Throwable cause) {
        super(cause);
    }

    public AppComponentsContainerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
