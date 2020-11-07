package ru.otus.homework.hw32.common.rmi;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.message.HandlersStoreSingleHandler;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.MsClientImpl;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;

import java.rmi.RemoteException;
import java.util.Optional;

@Slf4j
public class MessageSystemRegisterByRmiAdapter implements MessageSystemRegisterByRmi {

    private final MessageSystem messageSystem;
    private final CallbackRegistry callbackRegistry;

    public MessageSystemRegisterByRmiAdapter(MessageSystem messageSystem, CallbackRegistry callbackRegistry) {
        this.messageSystem = messageSystem;
        this.callbackRegistry = callbackRegistry;
    }

    @Override
    public void addClient(String clientId, HandleMessageByRmi handler) throws RemoteException {
        MsClientImpl msClient = new MsClientImpl(clientId, messageSystem, new HandlersStoreSingleHandler(new RequestHandler<ResultDataType>() {
            @Override
            @SneakyThrows
            public Optional<Message> handle(Message msg) {
                handler.handle(msg);
                return Optional.empty();
            }
        }), callbackRegistry);
        messageSystem.addClient(msClient);
    }

    @Override
    public void removeClient(String clientId) throws RemoteException {
        messageSystem.removeClient(clientId);
    }

    @Override
    public boolean newMessage(Message msg) throws RemoteException {
        return messageSystem.newMessage(msg);
    }

    @Override
    public int currentQueueSize() throws RemoteException {
        return messageSystem.currentQueueSize();
    }
}
