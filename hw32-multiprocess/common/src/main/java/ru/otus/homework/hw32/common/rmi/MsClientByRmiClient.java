package ru.otus.homework.hw32.common.rmi;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.MsClientImpl;
import ru.otus.messagesystem.message.Message;

import java.rmi.server.UnicastRemoteObject;

public class MsClientByRmiClient extends MsClientImpl {

    private final MessageSystemRegisterByRmi messageSystemRmi;
    private HandlerMessageByRmiServer handler;
    private HandleMessageByRmi handlerStub;

    public MsClientByRmiClient(String name, MessageSystemRegisterByRmi messageSystemRmi, HandlersStore handlersStore, CallbackRegistry callbackRegistry) {
        super(name, null, handlersStore, callbackRegistry);
        this.messageSystemRmi = messageSystemRmi;
    }

    @Override
    @SneakyThrows
    public boolean sendMessage(Message msg) {
        return messageSystemRmi.newMessage(msg);
    }

    @SneakyThrows
    public void register() {
        if (handler == null) {
            handler = new HandlerMessageByRmiServer(this);
            handlerStub = (HandleMessageByRmi) UnicastRemoteObject.exportObject(handler, 0);
            messageSystemRmi.addClient(getName(), handlerStub);
        }
    }

    @SneakyThrows
    public void unregister() {
        if (handler != null) {
            messageSystemRmi.removeClient(getName());
            UnicastRemoteObject.unexportObject(handler, false);
            handlerStub = null;
            handler = null;
        }
    }
}
