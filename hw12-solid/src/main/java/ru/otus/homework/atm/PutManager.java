package ru.otus.homework.atm;

public interface PutManager {

    void put(Iterable<? extends ATMCell> cells, Iterable<Denomination> denominations);
}
