package ru.otus.homework.atm.impl;

import org.junit.jupiter.api.Test;
import ru.otus.homework.atm.ATMCell;
import ru.otus.homework.atm.ATMException;
import ru.otus.homework.atm.Denomination;

import static org.junit.jupiter.api.Assertions.*;

class ATMCellWithLimitTest {

    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new ATMCellWithLimit(null, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new ATMCellWithLimit(Denomination.D50, -1, 5));
        assertThrows(IllegalArgumentException.class, () -> new ATMCellWithLimit(Denomination.D50, 0, 5));
        assertThrows(IllegalArgumentException.class, () -> new ATMCellWithLimit(Denomination.D50, 10, -3));
        assertThrows(IllegalArgumentException.class, () -> new ATMCellWithLimit(Denomination.D50, 10, 11));

        assertDoesNotThrow(() -> new ATMCellWithLimit(Denomination.D50, 10, 5));
        assertDoesNotThrow(() -> new ATMCellWithLimit(Denomination.D50, 10, 10));
        assertDoesNotThrow(() -> new ATMCellWithLimit(Denomination.D50, 10, 0));
        assertDoesNotThrow(() -> new ATMCellWithLimit(Denomination.D50, 1, 0));
        assertDoesNotThrow(() -> new ATMCellWithLimit(Denomination.D50, 1, 1));
    }

    @Test
    void testPut() {
        ATMCell ATMCell0 = new ATMCellWithLimit(Denomination.D50, 10, 10);
        assertThrows(ATMException.class, () -> ATMCell0.put(-10));
        assertThrows(ATMException.class, () -> ATMCell0.put(1));
        ATMCell0.put(0);


        ATMCell ATMCell1 = new ATMCellWithLimit(Denomination.D50, 10, 5);
        assertThrows(ATMException.class, () -> ATMCell1.put(-10));
        ATMCell1.put(1);
        assertEquals(6, ATMCell1.getCount());
        ATMCell0.put(0);
    }

    @Test
    void testCashOut() {
        ATMCell ATMCell0 = new ATMCellWithLimit(Denomination.D50, 10, 10);
        assertThrows(ATMException.class, () -> ATMCell0.cashOut(-100));
        assertThrows(ATMException.class, () -> ATMCell0.cashOut(100));

        ATMCell0.cashOut(5);
        assertEquals(5, ATMCell0.getCount());
        ATMCell0.cashOut(5);
        assertEquals(0, ATMCell0.getCount());

        assertThrows(ATMException.class, () -> ATMCell0.cashOut(-100));
        assertThrows(ATMException.class, () -> ATMCell0.cashOut(100));
    }

    @Test
    void testFree() {
        ATMCell ATMCell0 = new ATMCellWithLimit(Denomination.D50, 10, 10);
        assertEquals(0, ATMCell0.getFree());
        ATMCell0.cashOut(5);
        assertEquals(5, ATMCell0.getFree());
        ATMCell0.put(1);
        assertEquals(4, ATMCell0.getFree());
        ATMCell0.cashOut(6);
        assertEquals(ATMCell0.getLimit(), ATMCell0.getFree());
    }
}