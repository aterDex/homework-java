package ru.otus.homework.hw32.common.rmi;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.HandlersStoreSingleHandler;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.MsClientImpl;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;

import java.rmi.RemoteException;
import java.util.Optional;

@Slf4j
public class MessageSystemRegisterByRmiServer implements MessageSystemRegisterByRmi {

    private final MessageSystem messageSystem;
    private final CallbackRegistry callbackRegistry;

    public MessageSystemRegisterByRmiServer(MessageSystem messageSystem, CallbackRegistry callbackRegistry) {
        this.messageSystem = messageSystem;
        this.callbackRegistry = callbackRegistry;
    }

    @Override
    public void addClient(String clientId, HandleMessageByRmi handler) throws RemoteException {
        try {
            log.info("AddClient by rmi {}", clientId);
            MsClientImpl msClient = new MsClientImpl(clientId, messageSystem, new HandlersStoreSingleHandler(new RequestHandler<ResultDataType>() {
                @Override
                @SneakyThrows
                public Optional<Message> handle(Message msg) {
                    log.info("invoice before rmi handler");
                    handler.handle(msg);
                    return Optional.empty();
                }
            }), callbackRegistry);
            messageSystem.addClient(msClient);
        } catch (Exception e) {
            // TODO написать нормальный обработчик
            e.printStackTrace();
        }
    }

    @Override
    public void removeClient(String clientId) throws RemoteException {
        messageSystem.removeClient(clientId);
    }

    @Override
    public boolean newMessage(Message msg) throws RemoteException {
        return messageSystem.newMessage(msg);
    }
}
