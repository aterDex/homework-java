package ru.otus.homework.hw32.common.tcp;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.message.Message;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Function;

import static ru.otus.homework.hw32.common.tcp.SignalMessageHelper.*;

@Slf4j
public class MessageSystemOverSignalTcp implements MessageSystem {

    private final SignalTcpClient client;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ExecutorService executorForClients;
    private final Future<?> clientFuture;
    private final Map<String, MsClient> localBindings = new ConcurrentHashMap<>();

    public MessageSystemOverSignalTcp(String host, int port, ExecutorService executorForClients) {
        client = new SignalTcpClient(host, port, new SignalClientListenerImpl());
        clientFuture = executor.submit(client);
        this.executorForClients = executorForClients;
    }

    @Override
    @SneakyThrows
    public void addClient(MsClient msClient) {
        if (localBindings.containsKey(msClient.getName())) {
            throw new RuntimeException("msClient's name " + msClient.getName() + " is already exist");
        }
        try {
            sendSignalAndProcessAnswer(new Signal("addClient", UUID.randomUUID(), msClient.getName()), null);
            localBindings.put(msClient.getName(), msClient);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @SneakyThrows
    public void removeClient(String clientId) {
        if (localBindings.containsKey(clientId)) {
            throw new RuntimeException("msClient's name " + clientId + "haven't been exist yet");
        }
        sendSignalAndProcessAnswer(new Signal("removeClient", UUID.randomUUID(), clientId), null);
        localBindings.remove(clientId);
    }

    @Override
    @SneakyThrows
    public boolean newMessage(Message msg) {
        return sendSignalAndProcessAnswer(
                new Signal("newMessage", UUID.randomUUID(), msg),
                x -> (Boolean) x.getBody());
    }

    @Override
    public void dispose() throws InterruptedException {
        clientFuture.cancel(true);
    }

    @Override
    public void dispose(Runnable callback) throws InterruptedException {
        dispose();
        callback.run();
    }

    @Override
    public void start() {
        if (clientFuture.isDone()) {
            throw new RuntimeException("Message system isn't restartable.");
        }
    }

    @Override
    @SneakyThrows
    public int currentQueueSize() {
        return sendSignalAndProcessAnswer(
                new Signal("currentQueueSize", UUID.randomUUID(), null),
                x -> (Integer) x.getBody());
    }

    private <R> R sendSignalAndProcessAnswer(Signal signal, Function<Signal, R> converter) throws InterruptedException, TimeoutException, IOException {
        return processAnswer(signal, x -> {
            try {
                return client.sendAndGetAnswer(signal);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, converter);
    }

    private class SignalClientListenerImpl implements SignalClientListener {

        @Override
        public void event(Signal signal, SignalTcpClient client) {
            if (executorForClients == null) {
                handle(signal, client);
            } else {
                executorForClients.submit(() -> handle(signal, client));
            }
        }

        private void handle(Signal signal, SignalTcpClient client) {
            try {
                switch (signal.getTag()) {
                    case "handle":
                        try {
                            var msg = (Message) signal.getBody();
                            var msClient = localBindings.get(msg.getTo());
                            if (msClient == null) {
                                client.send(answerError(signal, String.format("Client '%s' doesn't exist.", msg.getTo())));
                                return;
                            }
                            msClient.handle(msg);
                            client.send(answerOk(signal));
                        } catch (Exception e) {
                            log.error("", e);
                            client.send(answerError(signal, e.getMessage()));
                        }
                        break;
                    case "error":
                        log.error("Error signal get: " + signal);
                        break;
                    default:
                        client.send(answerError(signal, String.format("Unknown signal %s", signal.getTag())));
                        break;
                }
            } catch (Exception e) {
                log.error("Error signal: " + signal, e);
            }
        }

        @Override
        public void closeConnect(SignalTcpClient client) {
            localBindings.clear();
        }
    }
}
