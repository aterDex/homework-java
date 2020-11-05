package ru.otus.homework.hw32.middle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmi;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmiServer;
import ru.otus.homework.hw32.middle.RmiRegistration;
import ru.otus.messagesystem.MessageSystem;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Configuration
public class MessageSystemRmiConfig {

    @Value("${middle.rmi.registry-port}")
    private int registryPort;

    @Value("${middle.rmi.message-system.name}")
    private String messageSystemName;

    @Bean
    public Registry registry() throws RemoteException {
        return LocateRegistry.createRegistry(registryPort);
    }

    @Bean
    public MessageSystemRegisterByRmi messageSystemRegisterByRmi(MessageSystem messageSystem) {
        return new MessageSystemRegisterByRmiServer(messageSystem, null);
    }

    @Bean(initMethod = "register", destroyMethod = "unregister")
    public RmiRegistration messageSystemRmiRegistration(Registry registry, MessageSystemRegisterByRmi messageSystemRmi) {
        return new RmiRegistration<>(registry, messageSystemRmi, messageSystemName);
    }
}
