package ru.otus.homework.atm;

public interface ATMSubscriber {

    void update(ATMEventType type, ATM atm);
}
