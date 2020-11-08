package ru.otus.homework.hw32.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.otus.messagesystem.MessageSystem;

@Getter
@AllArgsConstructor
public class DisposableMessageSystem {

    private final String description;
    private final MessageSystem messageSystem;
    private final Runnable dispose;
}
