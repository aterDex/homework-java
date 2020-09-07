package ru.otus.homework.atm;

public interface CashOutManager {

    void cashOut(long amount, Iterable<? extends ATMCell> cells);
}
