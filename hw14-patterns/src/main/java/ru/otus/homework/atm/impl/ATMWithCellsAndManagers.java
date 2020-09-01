package ru.otus.homework.atm.impl;

import ru.otus.homework.atm.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static ru.otus.homework.atm.ATMEventType.CASH_OUT;
import static ru.otus.homework.atm.ATMEventType.PUT;

public class ATMWithCellsAndManagers implements ATM {

    private final List<? extends ATMCell> cells;
    private final CashOutStrategy cashOutStrategy;
    private final PutStrategy putStrategy;
    private final List<ATMSubscriber> atmSubscribers = new ArrayList<>();

    public ATMWithCellsAndManagers(Collection<? extends ATMCell> cells, CashOutStrategy cashOutStrategy, PutStrategy putStrategy) {
        if (cells == null)
            throw new IllegalArgumentException("Cells mustn't be null.");
        if (cashOutStrategy == null)
            throw new IllegalArgumentException("CashOutStrategy mustn't be null.");
        if (putStrategy == null)
            throw new IllegalArgumentException("PutStrategy mustn't be null.");

        this.cells = Collections.unmodifiableList(new ArrayList<>(cells));
        this.cashOutStrategy = cashOutStrategy;
        this.putStrategy = putStrategy;
    }

    @Override
    public void put(Denomination denomination) {
        this.putAll(Collections.singleton(denomination));
        notify(PUT);
    }

    @Override
    public void putAll(Iterable<Denomination> denominations) {
        putStrategy.put(cells, denominations);
        notify(CASH_OUT);
    }

    @Override
    public void cashOut(long amount) {
        cashOutStrategy.cashOut(amount, cells);
    }

    @Override
    public long balance() {
        return cells.stream().mapToLong(ATMCell::getAmount).sum();
    }

    @Override
    public void subscribe(ATMSubscriber subscriber) {
        atmSubscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(ATMSubscriber subscriber) {
        atmSubscribers.remove(subscriber);
    }

    @Override
    public Snapshot createSnapshot() {
        return SnapshotDeMultiplexer.createFromMementos(cells);
    }

    protected void notify(ATMEventType type) {
        atmSubscribers.forEach(x -> x.update(type, this));
    }
}
