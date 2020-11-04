package ru.otus.homework.hw32.common.rmi;

import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.message.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SendMessageByRmiServer extends UnicastRemoteObject implements SendMessageByRmi {

    private final MessageSystem messageSystem;

    protected SendMessageByRmiServer(int port, MessageSystem messageSystem) throws RemoteException {
        super(port);
        this.messageSystem = messageSystem;
    }

    @Override
    public boolean sendMessage(Message msg) throws RemoteException {
        return messageSystem.newMessage(msg);
    }
}
