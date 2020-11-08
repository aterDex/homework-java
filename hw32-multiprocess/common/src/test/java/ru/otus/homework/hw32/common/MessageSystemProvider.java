package ru.otus.homework.hw32.common;

import ru.otus.messagesystem.MessageSystem;

public interface MessageSystemProvider {

    String getDescription();
    DisposableMessageSystem init(MessageSystem core) throws Exception;
}
