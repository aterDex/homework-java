package ru.otus.homework.hw32.middle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.otus.homework.hw32.common.tcp.MessageSystemOverSignalTcpAdapter;
import ru.otus.homework.hw32.common.tcp.SignalTcpServer;
import ru.otus.messagesystem.MessageSystem;

import java.util.concurrent.Executors;

@Configuration
@Profile("SignalTcp")
public class MessageSystemSignalTcpConfig {

    @Value("${signal-tcp.port}")
    private int port;

    @Value("${signal-tcp.host}")
    private String host;

    @Bean
    public SignalTcpServer signalTcpServer() {
        var server = new SignalTcpServer(host, port, 10000);
        Executors.newSingleThreadExecutor().submit(server);
        return server;
    }

    @Bean
    public MessageSystemOverSignalTcpAdapter messageSystemOverSignalTcpAdapter(MessageSystem messageSystem, SignalTcpServer server) {
        return new MessageSystemOverSignalTcpAdapter(server, messageSystem, null);
    }
}
