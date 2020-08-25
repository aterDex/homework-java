package ru.otus.homework.atm;

import java.time.ZonedDateTime;

public interface Snapshot {

    ZonedDateTime getDateCreate();

    void restore();
}
