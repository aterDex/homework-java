package ru.otus.homework.hw32.common.message;

import lombok.extern.slf4j.Slf4j;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.message.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Slf4j
public class MessageSystemRemote implements MessageSystem {

    private final TransportForMessageSystem transport;
    private final Map<String, MsClient> localClients = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private final TransportListener transportListener = new TransportListenerImpl();
    private volatile boolean active = false;

    public MessageSystemRemote(TransportForMessageSystem transport, ExecutorService executorService) {
        this.transport = transport;
        this.executorService = executorService;
    }

    @Override
    public void addClient(MsClient msClient) {
        checkState();
        // Чтобы дополнительно не сихронизировать проверку а затем добавление,
        // пытаемся сразу положить значение.
        if (localClients.putIfAbsent(msClient.getName(), msClient) != null) {
            throw new RuntimeException(String.format("'%s' is already there."));
        }
        try {
            transport.sendAddClient(msClient.getName());
        } catch (Exception t) {
            localClients.remove(msClient.getName());
        }
    }

    @Override
    public void removeClient(String clientId) {
        checkState();
        if (!localClients.containsKey(clientId)) {
            throw new RuntimeException(String.format("'%s' not found.", clientId));
        }
        transport.sendRemoveClient(clientId);
        localClients.remove(clientId);
    }

    @Override
    public boolean newMessage(Message msg) {
        checkState();
        if (localClients.containsKey(msg.getTo())) {
            return processMsgLocal(msg);
        }
        return transport.sendNewMessage(msg);
    }

    @Override
    public void dispose() throws InterruptedException {
        dispose(() -> {
        });
    }

    @Override
    public void dispose(Runnable callback) {
        if (active) {
            active = false;
            removeAllClients();
            transport.removeListener(transportListener);
            callback.run();
        }
    }

    @Override
    public void start() {
        active = true;
        transport.addListener(transportListener);
    }

    @Override
    public int currentQueueSize() {
        checkState();
        return transport.sendCurrentQueueSize();
    }

    private void removeAllClients() {
        for (String s : localClients.keySet()) {
            try {
                transport.sendRemoveClient(s);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        localClients.clear();
    }

    private boolean processMsgLocal(Message msg) {
        final var client = localClients.get(msg.getTo());
        if (client == null) {
            throw new RuntimeException(String.format("'%s' not found.", msg.getTo()));
        }
        if (executorService == null) {
            client.handle(msg);
        } else {
            executorService.submit(() -> client.handle(msg));
        }
        return true;
    }

    private void checkState() {
        if (!active) {
            throw new RuntimeException("Message system is down.");
        }
        if (!transport.isConnected()) {
            throw new RuntimeException("Message system isn't available now.");
        }
    }

    private class TransportListenerImpl implements TransportListener {

        @Override
        public void reconnect() {
        }

        @Override
        public void disconnect() {
        }

        @Override
        public void handle(Message message) {
            processMsgLocal(message);
        }
    }
}