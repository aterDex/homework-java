package ru.otus.homework.hw32.common.tcp;

import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

@Value
public class Signal implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tag;

    private UUID uuid;

    private Serializable body;
}
