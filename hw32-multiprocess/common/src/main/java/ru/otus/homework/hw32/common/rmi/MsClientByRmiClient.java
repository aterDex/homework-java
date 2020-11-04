package ru.otus.homework.hw32.common.rmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.MessageCallback;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageBuilder;
import ru.otus.messagesystem.message.MessageType;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.UUID;

public class MsClientByRmiClient implements MsClient {

    private static final Logger log = LoggerFactory.getLogger(MsClientByRmiClient.class);

    private SendMessageByRmi sendMessageByRmi;
    private final String name;
    private final RmiConnectInfo info;
    private final HandlersStore handlersStore;
    private final CallbackRegistry callbackRegistry;

    public MsClientByRmiClient(String name, int localRegistryPort, int serverPort, HandlersStore handlersStore, CallbackRegistry callbackRegistry) throws Exception {
        this.name = name;
        this.handlersStore = handlersStore;
        this.callbackRegistry = callbackRegistry;

        info = new RmiConnectInfo();
        info.setName("//localhost:" + localRegistryPort + "/handler/" + UUID.randomUUID().toString());
        HandleMessageByRmiServer handler = new HandleMessageByRmiServer(serverPort, this);
        Naming.rebind(info.getName(), handler);
    }

    @Override
    public boolean sendMessage(Message msg) {
        try {
            return sendMessageByRmi.sendMessage(msg);
        } catch (RemoteException e) {
            log.error("", e);
            return false;
        }
    }

    @Override
    public void handle(Message msg) {
        try {
            RequestHandler requestHandler = handlersStore.getHandlerByType(msg.getType());
            if (requestHandler != null) {
                requestHandler.handle(msg).ifPresent(message -> sendMessage((Message) message));
            } else {
                log.error("handler not found for the message type:{}", msg.getType());
            }
        } catch (Exception ex) {
            log.error("msg:{}", msg, ex);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T extends ResultDataType> Message produceMessage(String to, T data, MessageType msgType, MessageCallback<T> callback) {
        Message message = MessageBuilder.buildMessage(name, to, null, data, msgType);
        callbackRegistry.put(message.getCallbackId(), callback);
        return message;
    }

    public RmiConnectInfo getRmi() {
        return info;
    }

    public void initSender(RmiConnectInfo senderInfo) throws Exception {
        sendMessageByRmi = (SendMessageByRmi) Naming.lookup(senderInfo.getName());
    }
}
