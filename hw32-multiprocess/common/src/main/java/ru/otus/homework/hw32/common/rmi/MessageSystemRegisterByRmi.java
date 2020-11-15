package ru.otus.homework.hw32.common.rmi;

import ru.otus.messagesystem.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageSystemRegisterByRmi extends Remote {

    void addClient(String clientId, HandlerMessageByRmi handlerInfo) throws RemoteException;

    void removeClient(String clientId) throws RemoteException;

    boolean newMessage(Message msg) throws RemoteException;

    int currentQueueSize() throws RemoteException;
}
