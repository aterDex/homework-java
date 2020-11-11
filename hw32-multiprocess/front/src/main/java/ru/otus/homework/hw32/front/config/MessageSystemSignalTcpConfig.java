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

    @Bean(destroyMethod = "shutdownNow")
    public ExecutorService executorServiceForSignalTcpClient() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public SignalTcpClient signalClient(ExecutorService executorServiceForSignalTcpClient) throws Exception {
        var signalClient = new SignalTcpClient(host, port);
        executorServiceForSignalTcpClient.submit(signalClient);
        Thread.sleep(1000);
        return signalClient;
    }

    @Bean
    public TransportBySignalTcp transport(SignalTcpClient client) {
        return new TransportBySignalTcp(client);
    }

    @Bean(destroyMethod = "shutdownNow")
    public ExecutorService messageClientExecutorServices() {
        return Executors.newFixedThreadPool(3);
    }
}
