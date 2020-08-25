package impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.homework.atm.CashOutStrategy;
import ru.otus.homework.atm.Denomination;
import ru.otus.homework.atm.PutStrategy;
import ru.otus.homework.atm.impl.ATMCellWithLimit;
import ru.otus.homework.atm.impl.ATMWithCellsAndManagers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.otus.homework.atm.Denomination.*;

@ExtendWith(MockitoExtension.class)
class ATMWithCellsAndManagersTest {

    @Mock
    CashOutStrategy mockCashOut;
    @Mock
    PutStrategy mockPut;

    @Test
    void testBalance() {
        var atm = new ATMWithCellsAndManagers(
                List.of(new ATMCellWithLimit(D200, 10, 5),
                        new ATMCellWithLimit(Denomination.D100, 20, 20),
                        new ATMCellWithLimit(Denomination.D100, 10, 1),
                        new ATMCellWithLimit(Denomination.D2000, 10, 3)
                ), mockCashOut, mockPut
        );
        assertEquals(9100, atm.balance());
    }

    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new ATMWithCellsAndManagers(
                null, mockCashOut, mockPut));
        assertThrows(IllegalArgumentException.class, () -> new ATMWithCellsAndManagers(
                List.of(), null, mockPut));
        assertThrows(IllegalArgumentException.class, () -> new ATMWithCellsAndManagers(
                List.of(), mockCashOut, null));
    }

    @Test
    void testCashOut() {
        var atm = new ATMWithCellsAndManagers(
                List.of(), mockCashOut, mockPut);
        atm.cashOut(100L);
        Mockito.verify(mockCashOut).cashOut(Mockito.eq(100L), Mockito.any());
        Mockito.verifyNoMoreInteractions(mockCashOut);
        Mockito.verifyNoInteractions(mockPut);
    }

    @Test
    void testPutAndPutAll() {
        var atm = new ATMWithCellsAndManagers(
                List.of(), mockCashOut, mockPut);

        atm.put(D200);
        Mockito.verify(mockPut).put(Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(mockPut);
        Mockito.verifyNoInteractions(mockCashOut);

        Mockito.clearInvocations(mockPut);
        atm.putAll(List.of(D200, D200, D5000, D1000));
        Mockito.verify(mockPut).put(Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(mockPut);
        Mockito.verifyNoInteractions(mockCashOut);
    }

    @Test
    void testMemento() {
        var cells = List.of(new ATMCellWithLimit(D200, 10, 5),
                new ATMCellWithLimit(D100, 20, 10));
        var atm = new ATMWithCellsAndManagers(cells, mockCashOut, mockPut);
        assertEquals(2000, atm.balance());
        var snapshot = atm.createSnapshot();
        cells.get(0).put(1);
        cells.get(1).cashOut(1);
        assertEquals(2100, atm.balance());
        snapshot.restore();
        assertEquals(2000, atm.balance());
    }
}