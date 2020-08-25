package ru.otus.homework.atm;

public interface PutStrategy {

    void put(Iterable<? extends ATMCell> cells, Iterable<Denomination> denominations);
}
