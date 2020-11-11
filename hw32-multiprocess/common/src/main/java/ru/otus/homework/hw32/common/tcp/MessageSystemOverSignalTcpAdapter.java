package ru.otus.homework.hw32.common.tcp;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.message.HandlersStoreSingleHandler;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.MsClientImpl;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;

import java.io.IOException;
import java.util.*;

import static ru.otus.homework.hw32.common.tcp.SignalForMessageSystemHelper.*;
import static ru.otus.homework.hw32.common.tcp.SignalTypeForMessageSystem.ERROR;
import static ru.otus.homework.hw32.common.tcp.SignalTypeForMessageSystem.HANDLE;

@Slf4j
public class MessageSystemOverSignalTcpAdapter {

    private final SignalTcpServer server;
    private final MessageSystem messageSystem;
    private final CallbackRegistry callbackRegistry;
    private final Map<UUID, Set<String>> clients = new HashMap<>();

    public MessageSystemOverSignalTcpAdapter(SignalTcpServer server, MessageSystem messageSystem, CallbackRegistry callbackRegistry) {
        this.server = server;
        this.server.addListener(new SignalServerListenerImpl());
        this.messageSystem = messageSystem;
        this.callbackRegistry = callbackRegistry;
    }

    private void addClient(UUID connectIdentifier, Signal signal, SignalTcpServer server) throws IOException {
        try {
            String name = (String) signal.getBody();
            messageSystem.addClient(new MsClientImpl(name, messageSystem, new HandlersStoreSingleHandler(new RequestHandler<ResultDataType>() {
                @Override
                @SneakyThrows
                public Optional<Message> handle(Message msg) {
                    processAnswerServer(new Signal(HANDLE.name(), UUID.randomUUID(), msg), connectIdentifier, server, null);
                    return Optional.empty();
                }
            }), callbackRegistry));
            clients.computeIfAbsent(connectIdentifier, x -> new HashSet<>());
            clients.get(connectIdentifier).add(name);
            server.send(connectIdentifier, answerOk(signal));
        } catch (Exception e) {
            server.send(connectIdentifier, answerError(signal, e.getMessage()));
        }
    }

    private void removeClient(UUID connectIdentifier, Signal signal, SignalTcpServer server) throws IOException {
        try {
            String name = (String) signal.getBody();
            messageSystem.removeClient(name);
            clients.get(connectIdentifier).remove(name);
            server.send(connectIdentifier, answerOk(signal));
        } catch (Exception e) {
            server.send(connectIdentifier, answerError(signal, e.getMessage()));
        }
    }

    private void currentQueueSize(UUID connectIdentifier, Signal signal, SignalTcpServer server) throws IOException {
        try {
            Message name = (Message) signal.getBody();
            server.send(connectIdentifier, answerOk(signal, messageSystem.currentQueueSize()));
        } catch (Exception e) {
            server.send(connectIdentifier, answerError(signal, e.getMessage()));
        }
    }

    private void newMessage(UUID connectIdentifier, Signal signal, SignalTcpServer server) throws IOException {
        try {
            Message name = (Message) signal.getBody();
            server.send(connectIdentifier, answerOk(signal, messageSystem.newMessage(name)));
        } catch (Exception e) {
            server.send(connectIdentifier, answerError(signal, e.getMessage()));
        }
    }

    private class SignalServerListenerImpl implements SignalServerListener {

        @Override
        public void event(UUID connectIdentifier, Signal signal, SignalTcpServer server) {
            try {
                switch (SignalTypeForMessageSystem.valueOfOrUnknown(signal.getTag())) {
                    case ADD_CLIENT:
                        addClient(connectIdentifier, signal, server);
                        break;
                    case REMOVE_CLIENT:
                        removeClient(connectIdentifier, signal, server);
                        break;
                    case NEW_MESSAGE:
                        newMessage(connectIdentifier, signal, server);
                        break;
                    case CURRENT_QUEUE_SIZE:
                        currentQueueSize(connectIdentifier, signal, server);
                        break;
                    case ERROR:
                        log.error("Error signal get: " + signal);
                        break;
                    default:
                        server.send(connectIdentifier, new Signal(ERROR.name(), signal.getUuid(), String.format("Unknown signal %s", signal.getTag())));
                        break;
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }

        @Override
        public void disconnect(UUID connectWitchClose, SignalTcpServer server) {
            if (!clients.containsKey(connectWitchClose)) {
                return;
            }
            Set<String> list = clients.get(connectWitchClose);
            for (String client : list) {
                try {
                    messageSystem.removeClient(client);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
            clients.remove(connectWitchClose);
        }
    }
}
