package ru.otus.homework.atm.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.homework.atm.ATMException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.otus.homework.atm.Denomination.*;

class CashOutManagerFromAnyCallTest {


    private CashOutManagerFromAnyCall manager;

    @BeforeEach
    void before() {
        manager = new CashOutManagerFromAnyCall();
    }

    @Test
    void testOneCell() {
        ATMCellWithLimit cell0 = new ATMCellWithLimit(D1000, 10, 10);
        List<ATMCellWithLimit> cells0 = List.of(cell0);
        manager.cashOut(1000, cells0);
        assertEquals(9, cell0.getCount());
        manager.cashOut(9000, cells0);
        assertEquals(0, cell0.getCount());
    }

    @Test
    void testOneTypeCell() {
        ATMCellWithLimit cell0 = new ATMCellWithLimit(D1000, 10, 5);
        ATMCellWithLimit cell1 = new ATMCellWithLimit(D1000, 10, 5);
        List<ATMCellWithLimit> cells0 = List.of(cell0, cell1);
        manager.cashOut(1000, cells0);
        assertEquals(9, cell0.getCount() + cell1.getCount());
        manager.cashOut(8000, cells0);
        assertEquals(1, cell0.getCount() + cell1.getCount());
    }

    @Test
    void testDifferentTypeCell() {
        ATMCellWithLimit cell0 = new ATMCellWithLimit(D5000, 10, 5);
        ATMCellWithLimit cell1 = new ATMCellWithLimit(D1000, 10, 5);
        ATMCellWithLimit cell2 = new ATMCellWithLimit(D200, 10, 5);
        List<ATMCellWithLimit> cells0 = List.of(cell0, cell1, cell2);
        manager.cashOut(3400, cells0);
        assertEquals(5, cell0.getCount());
        assertEquals(2, cell1.getCount());
        assertEquals(3, cell2.getCount());
    }

    @Test
    void testDifferentTypeCellWithOverflowCell() {
        ATMCellWithLimit cell0 = new ATMCellWithLimit(D5000, 10, 5);
        ATMCellWithLimit cell1 = new ATMCellWithLimit(D1000, 10, 1);
        ATMCellWithLimit cell2 = new ATMCellWithLimit(D200, 15, 13);
        List<ATMCellWithLimit> cells0 = List.of(cell0, cell1, cell2);
        manager.cashOut(3400, cells0);
        assertEquals(5, cell0.getCount());
        assertEquals(0, cell1.getCount());
        assertEquals(1, cell2.getCount());
    }

    @Test
    void testDifferentTypeCellException() {
        ATMCellWithLimit cell0 = new ATMCellWithLimit(D5000, 10, 5);
        ATMCellWithLimit cell1 = new ATMCellWithLimit(D1000, 10, 1);
        ATMCellWithLimit cell2 = new ATMCellWithLimit(D200, 15, 13);
        List<ATMCellWithLimit> cells0 = List.of(cell0, cell1, cell2);
        assertThrows(ATMException.class, () -> manager.cashOut(3401, cells0));
        assertThrows(ATMException.class, () -> manager.cashOut(100000, cells0));
    }
}