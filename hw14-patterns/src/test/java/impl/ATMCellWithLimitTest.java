package impl;

import org.junit.jupiter.api.Test;
import ru.otus.homework.atm.ATMException;
import ru.otus.homework.atm.impl.ATMCellWithLimit;

import static org.junit.jupiter.api.Assertions.*;
import static ru.otus.homework.atm.Denomination.D50;

class ATMCellWithLimitTest {

    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new ATMCellWithLimit(null, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new ATMCellWithLimit(D50, -1, 5));
        assertThrows(IllegalArgumentException.class, () -> new ATMCellWithLimit(D50, 0, 5));
        assertThrows(IllegalArgumentException.class, () -> new ATMCellWithLimit(D50, 10, -3));
        assertThrows(IllegalArgumentException.class, () -> new ATMCellWithLimit(D50, 10, 11));

        assertDoesNotThrow(() -> new ATMCellWithLimit(D50, 10, 5));
        assertDoesNotThrow(() -> new ATMCellWithLimit(D50, 10, 10));
        assertDoesNotThrow(() -> new ATMCellWithLimit(D50, 10, 0));
        assertDoesNotThrow(() -> new ATMCellWithLimit(D50, 1, 0));
        assertDoesNotThrow(() -> new ATMCellWithLimit(D50, 1, 1));
    }

    @Test
    void testPut() {
        var atmCell0 = new ATMCellWithLimit(D50, 10, 10);
        assertThrows(ATMException.class, () -> atmCell0.put(-10));
        assertThrows(ATMException.class, () -> atmCell0.put(1));
        atmCell0.put(0);


        var atmCell1 = new ATMCellWithLimit(D50, 10, 5);
        assertThrows(ATMException.class, () -> atmCell1.put(-10));
        atmCell1.put(1);
        assertEquals(6, atmCell1.getCount());
        atmCell0.put(0);
    }

    @Test
    void testCashOut() {
        var atmCell0 = new ATMCellWithLimit(D50, 10, 10);
        assertThrows(ATMException.class, () -> atmCell0.cashOut(-100));
        assertThrows(ATMException.class, () -> atmCell0.cashOut(100));

        atmCell0.cashOut(5);
        assertEquals(5, atmCell0.getCount());
        atmCell0.cashOut(5);
        assertEquals(0, atmCell0.getCount());

        assertThrows(ATMException.class, () -> atmCell0.cashOut(-100));
        assertThrows(ATMException.class, () -> atmCell0.cashOut(100));
    }

    @Test
    void testFree() {
        var atmCell0 = new ATMCellWithLimit(D50, 10, 10);
        assertEquals(0, atmCell0.getFree());
        atmCell0.cashOut(5);
        assertEquals(5, atmCell0.getFree());
        atmCell0.put(1);
        assertEquals(4, atmCell0.getFree());
        atmCell0.cashOut(6);
        assertEquals(atmCell0.getLimit(), atmCell0.getFree());
    }

    @Test
    void testSnapshot() {
        var atmCell0 = new ATMCellWithLimit(D50, 10, 10);
        var snapshot10 = atmCell0.createSnapshot();
        atmCell0.cashOut(3);
        var snapshot7 = atmCell0.createSnapshot();
        atmCell0.put(1);
        var snapshot8 = atmCell0.createSnapshot();

        assertEquals(8, atmCell0.getCount());
        snapshot7.restore();
        assertEquals(7, atmCell0.getCount());
        snapshot10.restore();
        assertEquals(10, atmCell0.getCount());
        snapshot8.restore();
        assertEquals(8, atmCell0.getCount());
    }
}