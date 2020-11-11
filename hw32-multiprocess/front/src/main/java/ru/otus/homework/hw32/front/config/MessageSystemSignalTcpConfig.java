package ru.otus.homework.hw32.front.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.otus.homework.hw32.common.tcp.SignalTcpClient;
import ru.otus.homework.hw32.common.tcp.TransportBySignalTcp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Profile("SignalTcp")
public class MessageSystemSignalTcpConfig {

    @Value("${signal-tcp.port}")
    private int port;

    @Value("${signal-tcp.host}")
    private String host;

    @Bean
    public SignalTcpClient signalClient() {
        var signalClient = new SignalTcpClient(host, port);
        Executors.newSingleThreadExecutor().submit(signalClient);
        return signalClient;
    }

    @Bean
    public TransportBySignalTcp transport(SignalTcpClient client) {
        return new TransportBySignalTcp(client);
    }

    @Bean
    public ExecutorService messageClientExecutorServices() {
        return Executors.newFixedThreadPool(3);
    }
}
