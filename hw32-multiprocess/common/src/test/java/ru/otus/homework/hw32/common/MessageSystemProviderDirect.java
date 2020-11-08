package ru.otus.homework.hw32.common;

import ru.otus.messagesystem.MessageSystem;

public class MessageSystemProviderDirect implements MessageSystemProvider {

    private static final String description = "Direct";

    @Override
    public DisposableMessageSystem init(MessageSystem core) throws Exception {
        return new DisposableMessageSystem(description, core, () -> {
        });
    }

    @Override
    public String getDescription() {
        return description;
    }
}
