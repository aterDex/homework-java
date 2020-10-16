package ru.otus.homework.atm;

public interface ATM {

    void put(Denomination denomination);

    void putAll(Iterable<Denomination> denominations);

    void cashOut(long amount);

    long balance();
}
