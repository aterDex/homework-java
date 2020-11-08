package ru.otus.homework.hw32.middle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.otus.homework.hw32.common.tcp.MessageSystemOverSignalTcpAdapter;
import ru.otus.messagesystem.MessageSystem;

@Configuration
@Profile("SignalTcp")
public class MessageSystemSignalTcpConfig {

    @Value("${signal-tcp.port}")
    private int port;

    @Value("${signal-tcp.host}")
    private String host;

    @Bean(destroyMethod = "stop")
    public MessageSystemOverSignalTcpAdapter messageSystemOverSignalTcpAdapter(MessageSystem messageSystem) {
        return new MessageSystemOverSignalTcpAdapter(host, port, messageSystem, null);
    }
}
