package ru.otus.homework.hw32.common.message;

import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.MessageType;

public class HandlersStoreSingleHandler implements HandlersStore {

    private final RequestHandler<? extends ResultDataType> handler;

    public HandlersStoreSingleHandler(RequestHandler<? extends ResultDataType> handler) {
        this.handler = handler;
    }

    @Override
    public RequestHandler<? extends ResultDataType> getHandlerByType(String messageTypeName) {
        return handler;
    }

    @Override
    public void addHandler(MessageType messageType, RequestHandler<? extends ResultDataType> handler) {
        throw new UnsupportedOperationException("You shouldn't invoke addHandler for HandlersStoreSingleHandler. This can be only one handler.");
    }
}
