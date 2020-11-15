package ru.otus.homework.hw32.middle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmi;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmiAdapter;
import ru.otus.homework.hw32.middle.RmiRegistration;
import ru.otus.messagesystem.MessageSystem;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Configuration
@Profile("RMI")
public class MessageSystemRmiConfig {

    @Value("${rmi.registry-port}")
    private int registryPort;

    @Value("${rmi.message-system.name}")
    private String messageSystemName;

    @Bean
    public Registry registry() throws RemoteException {
        return LocateRegistry.createRegistry(registryPort);
    }

    @Bean
    public MessageSystemRegisterByRmi messageSystemRegisterByRmi(MessageSystem messageSystem) {
        return new MessageSystemRegisterByRmiAdapter(messageSystem, null);
    }

    @Bean(initMethod = "register", destroyMethod = "unregister")
    public RmiRegistration messageSystemRmiRegistration(Registry registry, MessageSystemRegisterByRmi messageSystemRmi) {
        return new RmiRegistration<>(registry, messageSystemRmi, messageSystemName);
    }
}
