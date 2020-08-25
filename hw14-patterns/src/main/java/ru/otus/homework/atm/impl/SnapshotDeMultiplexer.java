package ru.otus.homework.atm.impl;

import ru.otus.homework.atm.Memento;
import ru.otus.homework.atm.Snapshot;

import java.util.Arrays;
import java.util.Collection;

public class SnapshotDeMultiplexer extends SnapshotWithZonedDateTime {

    private final Snapshot[] innerSnapshots;

    public SnapshotDeMultiplexer(Snapshot[] innerSnapshots) {
        if (innerSnapshots == null) {
            this.innerSnapshots = new Snapshot[0];
        } else {
            this.innerSnapshots = Arrays.copyOf(innerSnapshots, innerSnapshots.length);
        }
    }

    public static SnapshotDeMultiplexer createFromMementos(Collection<? extends Memento> mementos) {
        if (mementos == null) return new SnapshotDeMultiplexer(null);
        return new SnapshotDeMultiplexer(mementos.stream().map(Memento::createSnapshot).toArray(Snapshot[]::new));
    }

    @Override
    public void restore() {
        Arrays.stream(innerSnapshots).forEach(Snapshot::restore);
    }
}
