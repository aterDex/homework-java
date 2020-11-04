package ru.otus.homework.hw32.common.rmi;

import ru.otus.messagesystem.MessageSystem;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Часть которую мы поднимаем на сервере, для регистрации клиентов messageSystem по rmi
 */
public class MessageSystemRegisterByRmiServer extends UnicastRemoteObject implements MessageSystemRegisterByRmi {

    private final int serverPort;
    private final int registryPort;

    private final MessageSystem messageSystem;

    public MessageSystemRegisterByRmiServer(int port, int registryPort, MessageSystem messageSystem) throws RemoteException {
        super(port);
        serverPort = port;
        this.registryPort = registryPort;
        this.messageSystem = messageSystem;
    }

    @Override
    public RmiConnectInfo addClient(String clientId, RmiConnectInfo handlerInfo) throws RemoteException {
        try {
            MsClientByRmiServer client = new MsClientByRmiServer(handlerInfo, registryPort, clientId, messageSystem, serverPort);
            messageSystem.addClient(client);
            return client.getRmi();
        } catch (Exception e) {
            // TODO написать нормальный обработчик
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void removeClient(String clientId) throws RemoteException {
        messageSystem.removeClient(clientId);
    }
}
