package ru.otus.homework.hw31.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.homework.hw31.data.core.service.DBServiceUser;
import ru.otus.homework.hw31.message.CreateUserRequestHandler;
import ru.otus.homework.hw31.message.CreateUserResponseHandler;
import ru.otus.homework.hw31.message.FrontendService;
import ru.otus.homework.hw31.message.FrontendServiceImpl;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.MessageSystemImpl;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.CallbackRegistryImpl;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.MsClientImpl;
import ru.otus.messagesystem.message.MessageType;

@Slf4j
@Configuration
public class MessageSystemConfig {

    @Value("${message-system.frontend-service-client-name}")
    private String frontendServiceClientName;
    @Value("${message-system.database-service-client-name}")
    private String databaseServiceClientName;

    @Bean
    public MessageSystem messageSystem() {
        return new MessageSystemImpl();
    }

    @Bean
    public CallbackRegistry callbackRegistry() {
        return new CallbackRegistryImpl();
    }

    @Bean
    public HandlersStore requestHandlerDatabaseStore(DBServiceUser dbService) {
        HandlersStore requestHandlerDatabaseStore = new HandlersStoreImpl();
        requestHandlerDatabaseStore.addHandler(MessageType.USER_DATA, new CreateUserRequestHandler(dbService));
        return requestHandlerDatabaseStore;
    }

    @Bean
    public MsClient databaseMsClient(
            MessageSystem messageSystem,
            CallbackRegistry callbackRegistry,
            @Qualifier("requestHandlerDatabaseStore") HandlersStore handler) {
        MsClientImpl client = new MsClientImpl(databaseServiceClientName,
                messageSystem, handler, callbackRegistry);
        messageSystem.addClient(client);
        return client;
    }

    @Bean
    public HandlersStore requestHandlerFrontendStore(CallbackRegistry callbackRegistry) {
        HandlersStore requestHandlerFrontendStore = new HandlersStoreImpl();
        requestHandlerFrontendStore.addHandler(MessageType.USER_DATA, new CreateUserResponseHandler(callbackRegistry));
        return requestHandlerFrontendStore;
    }

    @Bean
    public MsClient frontendMsClient(
            MessageSystem messageSystem,
            CallbackRegistry callbackRegistry,
            @Qualifier("requestHandlerFrontendStore") HandlersStore handler) {
        MsClientImpl client = new MsClientImpl(frontendServiceClientName,
                messageSystem, handler, callbackRegistry);
        messageSystem.addClient(client);
        return client;
    }

    @Bean
    public FrontendService frontendService(@Qualifier("frontendMsClient") MsClient client) {
        return new FrontendServiceImpl(client, databaseServiceClientName);
    }
}
