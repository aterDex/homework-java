package ru.otus.homework.atm.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.homework.atm.ATMException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.otus.homework.atm.Denomination.*;

class PutManagerToAnyATMCellTest {

    private PutManagerToAnyCell manager;

    @BeforeEach
    void before() {
        manager = new PutManagerToAnyCell();
    }

    @Test
    void testWorkKit() {
        List<ATMCellWithLimit> cells = List.of(
                new ATMCellWithLimit(D50, 100, 0),
                new ATMCellWithLimit(D100, 100, 0),
                new ATMCellWithLimit(D2000, 100, 0)
        );
        manager.put(cells,
                Arrays.asList(
                        D50, D100, D2000, D2000, D100, D50, D100, D100, D2000
                ));
        assertEquals(cells.get(0).getCount(), 2);
        assertEquals(cells.get(1).getCount(), 4);
        assertEquals(cells.get(2).getCount(), 3);
    }

    @Test
    void testNullPointerException() {
        assertThrows(NullPointerException.class, () -> manager.put(null, Collections.emptyList()));
        assertThrows(NullPointerException.class, () -> manager.put(Collections.emptyList(), null));

        manager.put(Collections.emptyList(), Collections.emptyList());
    }

    @Test
    void testDenominationNotSupportException() {
        List<ATMCellWithLimit> cells = List.of(
                new ATMCellWithLimit(D50, 100, 0),
                new ATMCellWithLimit(D100, 100, 0)
        );
        assertThrows(ATMException.class, () -> manager.put(cells, List.of(D200)));
    }

    @Test
    void testFreeSpaceException() {
        List<ATMCellWithLimit> cells = List.of(
                new ATMCellWithLimit(D50, 100, 100),
                new ATMCellWithLimit(D100, 100, 99)
        );
        assertThrows(ATMException.class, () -> manager.put(cells, List.of(D50)));
        assertThrows(ATMException.class, () -> manager.put(cells, List.of(D100, D100)));
    }

    @Test
    void testSeveralDenominationCellWithSameCost() {
        List<ATMCellWithLimit> cells = List.of(
                new ATMCellWithLimit(D100, 100, 100),
                new ATMCellWithLimit(D50, 100, 50),
                new ATMCellWithLimit(D50, 100, 50),
                new ATMCellWithLimit(D100, 100, 100)
        );

        manager.put(cells, Stream.generate(() -> D50).limit(10).collect(Collectors.toList()));
        assertEquals(110, cells.get(1).getCount() + cells.get(2).getCount());
        manager.put(cells, Stream.generate(() -> D50).limit(50).collect(Collectors.toList()));
        assertEquals(160, cells.get(1).getCount() + cells.get(2).getCount());
        manager.put(cells, Stream.generate(() -> D50).limit(40).collect(Collectors.toList()));
        assertEquals(200, cells.get(1).getCount() + cells.get(2).getCount());
    }
}