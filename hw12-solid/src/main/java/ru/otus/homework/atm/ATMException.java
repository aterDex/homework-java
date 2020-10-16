package ru.otus.homework.atm;

public class ATMException extends RuntimeException {

    public ATMException() {
    }

    public ATMException(String message) {
        super(message);
    }

    public ATMException(String message, Throwable cause) {
        super(message, cause);
    }

    public ATMException(Throwable cause) {
        super(cause);
    }

    public ATMException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
