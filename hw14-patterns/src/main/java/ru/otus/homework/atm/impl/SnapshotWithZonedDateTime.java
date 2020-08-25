package ru.otus.homework.atm.impl;

import ru.otus.homework.atm.Snapshot;

import java.time.ZonedDateTime;

public abstract class SnapshotWithZonedDateTime implements Snapshot {

    private final ZonedDateTime createDate = ZonedDateTime.now();

    @Override
    public ZonedDateTime getDateCreate() {
        return createDate;
    }
}
