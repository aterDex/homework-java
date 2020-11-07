package ru.otus.homework.hw32.common.tcp;

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
import java.util.concurrent.*;
import java.util.function.Function;

import static ru.otus.homework.hw32.common.tcp.SignalMessageHelper.*;

@Slf4j
public class MessageSystemOverSignalTcpAdapter {

    private final SignalTcpServer server;
    private final MessageSystem messageSystem;
    private final CallbackRegistry callbackRegistry;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<UUID, Set<String>> clients = new HashMap<>();
    private final Future<?> futureServer;

    public MessageSystemOverSignalTcpAdapter(String host, int port, MessageSystem messageSystem, CallbackRegistry callbackRegistry) {
        this.server = new SignalTcpServer(host, port, 10000, new SignalServerListenerImpl());
        this.messageSystem = messageSystem;
        this.callbackRegistry = callbackRegistry;
        futureServer = executor.submit(server);
    }

    public void stop() {
        futureServer.cancel(true);
    }

    private <R> R sendSignalAndProcessAnswer(UUID uuid, Signal signal, Function<Signal, R> converter) throws InterruptedException, TimeoutException, IOException {
        return processAnswer(signal, x -> {
            try {
                return server.sendAndGetAnswer(uuid, signal);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, converter);
    }

    private class SignalServerListenerImpl implements SignalServerListener {

        @Override
        public void event(UUID connectIdentifier, Signal signal, SignalTcpServer server) {
            try {
                switch (signal.getTag()) {
                    case "addClient":
                        try {
                            String name = (String) signal.getBody();
                            messageSystem.addClient(new MsClientImpl(name, messageSystem, new HandlersStoreSingleHandler(new RequestHandler<ResultDataType>() {
                                @Override
                                @SneakyThrows
                                public Optional<Message> handle(Message msg) {
                                    sendSignalAndProcessAnswer(connectIdentifier, new Signal("handle", UUID.randomUUID(), msg), null);
                                    return Optional.empty();
                                }
                            }), callbackRegistry));
                            clients.computeIfAbsent(connectIdentifier, x -> new HashSet<>());
                            clients.get(connectIdentifier).add(name);
                            server.send(connectIdentifier, answerOk(signal));
                        } catch (Exception e) {
                            server.send(connectIdentifier, answerError(signal, e.getMessage()));
                        }
                        break;
                    case "removeClient":
                        try {
                            String name = (String) signal.getBody();
                            messageSystem.removeClient(name);
                            clients.get(connectIdentifier).remove(name);
                            server.send(connectIdentifier, answerOk(signal));
                        } catch (Exception e) {
                            server.send(connectIdentifier, answerError(signal, e.getMessage()));
                        }
                        break;
                    case "newMessage":
                        try {
                            Message name = (Message) signal.getBody();
                            server.send(connectIdentifier, answerOk(signal, messageSystem.newMessage(name)));
                        } catch (Exception e) {
                            server.send(connectIdentifier, answerError(signal, e.getMessage()));
                        }
                        break;
                    case "currentQueueSize":
                        try {
                            Message name = (Message) signal.getBody();
                            server.send(connectIdentifier, answerOk(signal, messageSystem.currentQueueSize()));
                        } catch (Exception e) {
                            server.send(connectIdentifier, answerError(signal, e.getMessage()));
                        }
                        break;
                    case "error":
                        log.error("Error signal get: " + signal);
                        break;
                    default:
                        server.send(connectIdentifier, new Signal("error", signal.getUuid(), String.format("Unknown signal %s", signal.getTag())));
                        break;
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }

        @Override
        public void closeConnect(UUID connectWitchClose, SignalTcpServer server) {
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
