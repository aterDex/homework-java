package ru.otus.homework.hw32.common.rmi;

import ru.otus.messagesystem.client.MsClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageSystemRegisterByRmi extends Remote {

    RmiConnectInfo addClient(String clientId, RmiConnectInfo handlerInfo) throws RemoteException;

    void removeClient(String clientId) throws RemoteException;
}
