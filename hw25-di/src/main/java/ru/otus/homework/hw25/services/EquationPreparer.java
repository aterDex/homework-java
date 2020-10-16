package ru.otus.homework.hw25.services;

import ru.otus.homework.hw25.model.Equation;

import java.util.List;

public interface EquationPreparer {
    List<Equation> prepareEquationsFor(int base);
}
