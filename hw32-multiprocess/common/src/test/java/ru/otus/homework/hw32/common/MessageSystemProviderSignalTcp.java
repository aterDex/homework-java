package ru.otus.homework.hw32.common;

import lombok.SneakyThrows;
import ru.otus.homework.hw32.common.message.MessageSystemRemote;
import ru.otus.homework.hw32.common.tcp.MessageSystemOverSignalTcpAdapter;
import ru.otus.homework.hw32.common.tcp.SignalTcpClient;
import ru.otus.homework.hw32.common.tcp.SignalTcpServer;
import ru.otus.homework.hw32.common.tcp.TransportBySignalTcp;
import ru.otus.messagesystem.MessageSystem;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        SignalTcpServer server = new SignalTcpServer(host, port, 10000);
        ExecutorService executorServer = Executors.newSingleThreadExecutor();
        var futureServer = executorServer.submit(server);

        new MessageSystemOverSignalTcpAdapter(server, core, null);
        Thread.sleep(1000);
        int localPort = port;
        if (localPort == 0) {
            localPort = ((InetSocketAddress) server.getLocalAddress()).getPort();
        }
        var client = new SignalTcpClient(host, localPort);
        var executor = Executors.newFixedThreadPool(1);
        var futureClient = executor.submit(client);

        TransportBySignalTcp transportBySignalTcp = new TransportBySignalTcp(client);

        var messageSystem = new MessageSystemRemote(transportBySignalTcp, executorService);
        messageSystem.start();
        return new DisposableMessageSystem(description, messageSystem, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                messageSystem.dispose();
                futureClient.cancel(true);
                futureServer.cancel(true);
            }
        });
    }

    @Override
    public String getDescription() {
        return description;
    }
}
