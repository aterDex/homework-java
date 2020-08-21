package ru.otus.homework.atm.impl;

import ru.otus.homework.atm.ATMCell;
import ru.otus.homework.atm.ATMException;
import ru.otus.homework.atm.Denomination;

public class ATMCellWithLimit implements ATMCell {

    private final Denomination denomination;
    private final int limit;
    private int count;

    public ATMCellWithLimit(Denomination denomination, int limit, int count) {
        if (denomination == null)
            throw new IllegalArgumentException("Denomination mustn't be null.");
        if (limit < 1)
            throw new IllegalArgumentException("Limit mustn't be less 1.");
        if (count < 0)
            throw new IllegalArgumentException("Count mustn't be less 0.");
        if (count > limit)
            throw new IllegalArgumentException("Count mustn't be less or equals limit.");

        this.denomination = denomination;
        this.count = count;
        this.limit = limit;
    }

    @Override
    public Denomination getDenomination() {
        return denomination;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void put(int count) {
        if (count < 0)
            throw new ATMException(new IllegalArgumentException("Count mustn't be less 0."));
        if (!checkPut(count))
            throw new ATMException("Cell full.");

        this.count += count;
    }

    @Override
    public void cashOut(int count) {
        if (count < 0 || this.count < count)
            throw new ATMException(new IllegalArgumentException("Count mustn't be less 0 and more cell.count"));
        this.count -= count;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public int getFree() {
        return limit - count;
    }

    @Override
    public long getAmount() {
        return getDenomination().getCost() * getCount();
    }

    private boolean checkPut(int count) {
        int sum = this.count + count;
        return count >= 0 && sum >= 0 && sum <= limit;
    }
}
