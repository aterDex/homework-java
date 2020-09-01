package ru.otus.homework.department.impl;

import ru.otus.homework.atm.ATM;
import ru.otus.homework.atm.Snapshot;
import ru.otus.homework.atm.impl.SnapshotDeMultiplexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ATMDepartmentImpl {

    private final List<? extends ATM> atms;
    private final Snapshot firstSnapshot;

    public ATMDepartmentImpl(Collection<? extends ATM> atms) {
        if (atms == null)
            throw new IllegalArgumentException("Atms mustn't be null.");
        this.atms = Collections.unmodifiableList(new ArrayList<>(atms));
        this.atms.forEach(x -> x.subscribe((type, atm) -> {
            System.out.println(type + " " + atm.balance());
        }));
        this.firstSnapshot = SnapshotDeMultiplexer.createFromMementos(atms);
    }

    public void restoreAtms() {
        this.firstSnapshot.restore();
    }
}
