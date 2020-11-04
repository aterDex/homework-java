package ru.otus.homework.hw32.common.rmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.client.MessageCallback;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageType;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.UUID;

public class MsClientByRmiServer implements MsClient {

    private static final Logger log = LoggerFactory.getLogger(MsClientByRmiServer.class);

    private final HandleMessageByRmi handle;
    private final String name;
    private final RmiConnectInfo connectInfo;
    private final RmiConnectInfo handlerInfo;
    private final SendMessageByRmiServer sender;
    private final MessageSystem messageSystem;

    public MsClientByRmiServer(RmiConnectInfo handler, int registryPort , String name, MessageSystem messageSystem, int serverPort) throws Exception {
        this.handle = (HandleMessageByRmi) Naming.lookup(handler.getName());
        this.name = name;
        this.handlerInfo = handler;
        this.messageSystem = messageSystem;
        connectInfo = new RmiConnectInfo();
        connectInfo.setName("//localhost:" + registryPort + "/sender/" + UUID.randomUUID().toString());

        sender = new SendMessageByRmiServer(serverPort, messageSystem);
        Naming.rebind(connectInfo.getName(), sender);
    }

    @Override
    public boolean sendMessage(Message msg) {
        // TODO описать тут ошибку!
        throw new UnsupportedOperationException("");
    }

    @Override
    public void handle(Message msg) {
        try {
            log.info("handle {} for {}", msg, handle);
            handle.handle(msg);
        } catch (RemoteException e) {
            log.error("", e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T extends ResultDataType> Message produceMessage(String to, T data, MessageType msgType, MessageCallback<T> callback) {
        throw new UnsupportedOperationException("");
    }

    public RmiConnectInfo getRmi() {
        return connectInfo;
    }
}
