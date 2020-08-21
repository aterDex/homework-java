package ru.otus.homework.atm.impl;

import ru.otus.homework.atm.ATMException;
import ru.otus.homework.atm.ATMCell;
import ru.otus.homework.atm.Denomination;
import ru.otus.homework.atm.PutManager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Простой алгоритм для зачисления средств.
 * Находит первую попавшуюся ячейку в которую могут быть зачисленны средства и зачисляет в нее максимум сколько может.
 * Если зачисление не возможно будет ошибка ATMException.
 */
public class PutManagerToAnyCell implements PutManager {

    @Override
    public void put(Iterable<? extends ATMCell> cells, Iterable<Denomination> denominations) {
        Map<Denomination, Long> denominationGroupAndCount = StreamSupport.stream(denominations.spliterator(), false)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<Denomination, List<ATMCell>> cellsGroupByDenomination = StreamSupport.stream(cells.spliterator(), false)
                .collect(Collectors.groupingBy(ATMCell::getDenomination, Collectors.toList()));

        checkAvailableCellsAndFreeSpaceInCells(denominationGroupAndCount, cellsGroupByDenomination);
        enrollment(denominationGroupAndCount, cellsGroupByDenomination);
    }

    private void checkAvailableCellsAndFreeSpaceInCells(Map<Denomination, Long> denominationGroupByDenomination, Map<Denomination, List<ATMCell>> cellsGroupByDenomination) {
        Set<Denomination> denominationAvailable = cellsGroupByDenomination.keySet();
        denominationGroupByDenomination.entrySet().stream()
                .peek(x -> {
                    if (!denominationAvailable.contains(x.getKey()))
                        throw new ATMException(String.format("Denomination '%s' not support.", x.getKey()));
                })
                .filter(x -> cellsGroupByDenomination.get(x.getKey()).stream().mapToLong(ATMCell::getFree).sum() < x.getValue())
                .findAny()
                .map(Map.Entry::getKey)
                .ifPresent(denomination -> {
                            throw new ATMException(String.format("Not found free space for denomination '%s'.", denomination));
                        }
                );
    }

    private void enrollment(Map<Denomination, Long> denominationGroupAndCount, Map<Denomination, List<ATMCell>> cellsGroupByDenomination) {
        denominationGroupAndCount.entrySet().stream().forEach(x -> {
            var balance = x.getValue().longValue();
            List<ATMCell> dm = cellsGroupByDenomination.get(x.getKey());
            for (ATMCell ATMCell : dm) {
                int free = ATMCell.getFree();
                if (balance <= free) {
                    ATMCell.put((int) balance);
                    break;
                }
                balance -= free;
                ATMCell.put(free);
            }
        });
    }
}
