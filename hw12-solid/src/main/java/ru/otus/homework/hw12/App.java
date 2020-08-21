package ru.otus.homework.hw12;

import ru.otus.homework.atm.ATM;
import ru.otus.homework.atm.ATMException;
import ru.otus.homework.atm.Denomination;
import ru.otus.homework.atm.impl.ATMCellWithLimit;
import ru.otus.homework.atm.impl.ATMWithCellsAndManagers;
import ru.otus.homework.atm.impl.CashOutManagerFromAnyCall;
import ru.otus.homework.atm.impl.PutManagerToAnyCell;

import java.util.List;

import static ru.otus.homework.atm.Denomination.*;

public class App {

    public static void main(String[] args) {
        ATM atm = buildATM();
        System.out.printf("Welcome! Balance is %s.", atm.balance()).println();
        cashOutWithLog(atm, 14500);
        putAllWithLog(atm, List.of(D5000, D100));
        cashOutWithLog(atm, 50);
        putAllWithLog(atm, List.of(D50, D50, D50, D50, D50));
        cashOutWithLog(atm, 50);
        putAllWithLog(atm, List.of(D50, D50, D50, D50, D50));
        cashOutWithLog(atm, 25450);
        System.out.printf("Goodbye! Balance is %s.", atm.balance()).println();
    }

    private static void putAllWithLog(ATM atm, Iterable<Denomination> collections) {
        try {
            atm.putAll(collections);
            System.out.printf("PutAll %s. Balance is %s.", collections, atm.balance()).println();
        } catch (ATMException e) {
            System.out.printf("I'm sorry. You can't putAll %s. Balance %s.", collections, atm.balance()).println();
        }
    }

    private static void cashOutWithLog(ATM atm, long cashOut) {
        try {
            atm.cashOut(cashOut);
            System.out.printf("CashOut %s. Balance is %s.", cashOut, atm.balance()).println();
        } catch (ATMException e) {
            System.out.printf("I'm sorry. You can't cashOut %s. Balance %s.", cashOut, atm.balance()).println();
        }
    }

    private static ATM buildATM() {
        return new ATMWithCellsAndManagers(List.of(
                new ATMCellWithLimit(D50, 5, 0),
                new ATMCellWithLimit(D100, 22, 22),
                new ATMCellWithLimit(D100, 10, 0),
                new ATMCellWithLimit(D200, 10, 2),
                new ATMCellWithLimit(D500, 20, 19),
                new ATMCellWithLimit(D1000, 50, 35),
                new ATMCellWithLimit(D2000, 25, 25),
                new ATMCellWithLimit(D5000, 40, 40)
        ), new CashOutManagerFromAnyCall(), new PutManagerToAnyCell());
    }
}
