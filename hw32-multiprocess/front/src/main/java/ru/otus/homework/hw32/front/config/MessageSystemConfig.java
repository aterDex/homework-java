package ru.otus.homework.hw32.front.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.homework.hw32.common.message.CallbackRequestHandler;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.CallbackRegistryImpl;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.MsClientImpl;
import ru.otus.messagesystem.message.MessageType;

@Configuration
public class MessageSystemConfig {

    @Value("${message-system.frontend-service-client-name}")
    private String frontendServiceClientName;

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

    @Bean
    public MsClient databaseMsClient(
            MessageSystem messageSystem,
            @Qualifier("requestHandlerFrontendStore") HandlersStore handler,
            CallbackRegistry callbackRegistry) {
        MsClientImpl client = new MsClientImpl(frontendServiceClientName, messageSystem, handler, callbackRegistry);
        messageSystem.addClient(client);
        return client;
    }
}
