package ru.otus.homework.hw32.common;

import lombok.SneakyThrows;
import ru.otus.homework.hw32.common.tcp.MessageSystemOverSignalTcp;
import ru.otus.homework.hw32.common.tcp.MessageSystemOverSignalTcpAdapter;
import ru.otus.messagesystem.MessageSystem;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

public class MessageSystemProviderSignalTcp implements MessageSystemProvider {

    private static final String description = "SignalTcp";
    private final String host;
    private final int port;
    private final ExecutorService executorService;

    public MessageSystemProviderSignalTcp(String host, int port, ExecutorService executorService) {
        this.host = host;
        this.port = port;
        this.executorService = executorService;
    }

    @Override
    public DisposableMessageSystem init(MessageSystem core) throws Exception {
        var adapter = new MessageSystemOverSignalTcpAdapter(host, port, core, null);
        Thread.sleep(1000);
        int localPort = port;
        if (localPort == 0) {
            localPort = ((InetSocketAddress) adapter.getServer().getLocalAddress()).getPort();
        }
        var messageSystem = new MessageSystemOverSignalTcp(host, localPort, executorService);
        return new DisposableMessageSystem(description, core, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                messageSystem.dispose();
                adapter.stop();
            }
        });
    }

    @Override
    public String getDescription() {
        return description;
    }
}
