package ru.otus.homework.atm;

public interface CashOutStrategy {

    void cashOut(long amount, Iterable<? extends ATMCell> cells);
}
