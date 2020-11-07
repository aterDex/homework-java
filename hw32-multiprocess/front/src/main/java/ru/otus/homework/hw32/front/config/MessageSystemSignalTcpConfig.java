package ru.otus.homework.hw32.front.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.otus.homework.hw32.common.tcp.MessageSystemOverSignalTcp;
import ru.otus.messagesystem.MessageSystem;

import java.util.concurrent.Executors;

@Configuration
@Profile("SignalTcp")
public class MessageSystemSignalTcpConfig {

    @Value("${signal-tcp.port}")
    private int port;

    @Value("${signal-tcp.host}")
    private String host;

    @Bean(destroyMethod = "dispose")
    public MessageSystem messageSystem() {
        return new MessageSystemOverSignalTcp(host, port, Executors.newCachedThreadPool());
    }
}
