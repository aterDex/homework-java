package ru.otus.homework.hw32.common.tcp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class Signal implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tag;

    private boolean answer;

    private UUID uuid;

    private Serializable body;
}
