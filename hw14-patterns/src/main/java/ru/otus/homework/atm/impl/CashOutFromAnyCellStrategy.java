package ru.otus.homework.atm.impl;

import ru.otus.homework.atm.ATMCell;
import ru.otus.homework.atm.ATMException;
import ru.otus.homework.atm.CashOutStrategy;
import ru.otus.homework.atm.Denomination;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Простой алгоритм для снятия средств.
 * Если не возможно выдать сумму, кидает ошибку ATMException.
 * В первую очередь выдаем крупные купюры.
 */
public class CashOutFromAnyCellStrategy implements CashOutStrategy {

    @Override
    public void cashOut(long amount, Iterable<? extends ATMCell> cells) {
        Map<Denomination, List<ATMCell>> cellsGroupAndSortByDenominationDesc = StreamSupport.stream(cells.spliterator(), false)
                .collect(Collectors.groupingBy(ATMCell::getDenomination, () -> new TreeMap<>(Comparator.comparingInt(Denomination::getCost).reversed()), Collectors.toList()));

        createTasksForCashOut(amount, cellsGroupAndSortByDenominationDesc)
                .forEach(TaskForCashOut::execute);
    }

    private Collection<TaskForCashOut> createTasksForCashOut(long amount, Map<Denomination, List<ATMCell>> cellsGroupAndSortByDenominationDesc) {
        var tasks = new ArrayList<TaskForCashOut>();

        var balance = amount;
        for (var denominationCells : cellsGroupAndSortByDenominationDesc.entrySet()) {
            int cost = denominationCells.getKey().getCost();
            if (cost > balance) continue;

            var cashOut = (int) (balance / cost);
            for (var workCell : denominationCells.getValue()) {
                var cashOutForCell = Math.min(workCell.getCount(), cashOut);
                if (cashOutForCell > 0) {
                    tasks.add(new TaskForCashOut(workCell, cashOutForCell));
                    balance -= cashOutForCell * cost;
                    cashOut -= cashOutForCell;
                    if (cashOut == 0) break;
                }
            }
        }
        if (balance != 0) {
            throw new ATMException(String.format("Amount '%s' unavailable.", amount));
        }
        return tasks;
    }

    private class TaskForCashOut {

        private final ATMCell ATMCell;
        private final int cashOut;

        public TaskForCashOut(ATMCell ATMCell, int cashOut) {
            this.ATMCell = ATMCell;
            this.cashOut = cashOut;
        }

        public void execute() {
            ATMCell.cashOut(cashOut);
        }
    }
}
