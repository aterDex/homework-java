package ru.otus.homework.atm;

public interface ATM extends Memento {

    void put(Denomination denomination);

    void putAll(Iterable<Denomination> denominations);

    void cashOut(long amount);

    long balance();

    void subscribe(ATMSubscriber subscriber);

    void unsubscribe(ATMSubscriber subscriber);
}
