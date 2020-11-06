package ru.otus.homework.hw32.common.tcp;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Signal implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tag;

    private Serializable body;
}
