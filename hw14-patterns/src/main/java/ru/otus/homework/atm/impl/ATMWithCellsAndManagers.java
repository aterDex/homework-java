package ru.otus.homework.atm.impl;

import ru.otus.homework.atm.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ATMWithCellsAndManagers implements ATM {

    private final List<? extends ATMCell> cells;
    private final CashOutStrategy cashOutStrategy;
    private final PutStrategy putStrategy;

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
    }

    @Override
    public void putAll(Iterable<Denomination> denominations) {
        putStrategy.put(cells, denominations);
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
    public Snapshot createSnapshot() {
        return SnapshotDeMultiplexer.createFromMementos(cells);
    }
}
