package ru.otus.homework.atm.impl;

import ru.otus.homework.atm.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ATMWithCellsAndManagers implements ATM {

    private final List<? extends ATMCell> cells;
    private final CashOutManager cashOutManager;
    private final PutManager putManager;

    public ATMWithCellsAndManagers(Collection<? extends ATMCell> cells, CashOutManager cashOutManager, PutManager putManager) {
        if (cells == null)
            throw new IllegalArgumentException("Cells mustn't be null.");
        if (cashOutManager == null)
            throw new IllegalArgumentException("Cells mustn't be null.");
        if (putManager == null)
            throw new IllegalArgumentException("Cells mustn't be null.");

        this.cells = Collections.unmodifiableList(new ArrayList<>(cells));
        this.cashOutManager = cashOutManager;
        this.putManager = putManager;
    }

    @Override
    public void put(Denomination denomination) {
        this.putAll(Collections.singleton(denomination));
    }

    @Override
    public void putAll(Iterable<Denomination> denominations) {
        putManager.put(cells, denominations);
    }

    @Override
    public void cashOut(long amount) {
        cashOutManager.cashOut(amount, cells);
    }

    @Override
    public long balance() {
        return cells.stream().mapToLong(ATMCell::getAmount).sum();
    }
}
