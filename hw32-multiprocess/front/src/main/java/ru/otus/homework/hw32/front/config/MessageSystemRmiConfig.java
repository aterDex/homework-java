package ru.otus.homework.hw32.front.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.homework.hw32.common.CallbackRequestHandler;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmi;
import ru.otus.homework.hw32.common.rmi.MsClientByRmiClient;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.CallbackRegistryImpl;
import ru.otus.messagesystem.message.MessageType;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Configuration
public class MessageSystemRmiConfig {

    @Value("${rmi.registry-port}")
    private int registryPort;

    @Value("${rmi.message-system.name}")
    private String messageSystemName;

    @Value("${message-system.frontend-service-client-name}")
    private String frontendServiceClientName;

    @Bean
    public Registry registry() throws RemoteException {
        return LocateRegistry.getRegistry(registryPort);
    }

    @Bean
    public MessageSystemRegisterByRmi messageSystemRegister(Registry registry) throws Exception {
        return (MessageSystemRegisterByRmi) registry.lookup(messageSystemName);
    }

    @Bean
    public HandlersStore requestHandlerFrontendStore(CallbackRegistry callbackRegistry) {
        HandlersStore requestHandlerFrontendStore = new HandlersStoreImpl();
        requestHandlerFrontendStore.addHandler(MessageType.USER_DATA, new CallbackRequestHandler(callbackRegistry));
        return requestHandlerFrontendStore;
    }

    @Bean
    public CallbackRegistry callbackRegistry() {
        return new CallbackRegistryImpl();
    }

    @Bean(initMethod = "register", destroyMethod = "unregister")
    public MsClientByRmiClient databaseMsClient(
            MessageSystemRegisterByRmi messageSystemRegister,
            @Qualifier("requestHandlerFrontendStore") HandlersStore handler,
            CallbackRegistry callbackRegistry) {
        return new MsClientByRmiClient(frontendServiceClientName, messageSystemRegister, handler, callbackRegistry);
    }
}
