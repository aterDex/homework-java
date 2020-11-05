package ru.otus.homework.hw32.common.rmi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.otus.homework.hw32.common.CallbackRequestHandler;
import ru.otus.messagesystem.*;
import ru.otus.messagesystem.client.*;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageBuilder;
import ru.otus.messagesystem.message.MessageHelper;
import ru.otus.messagesystem.message.MessageType;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class TtTest {

    private static final int REGISTRY_PORT = 1099;

    private static final String FRONTEND_SERVICE_CLIENT_NAME = "frontendService";
    private static final String DATABASE_SERVICE_CLIENT_NAME = "databaseService";

    private MessageSystem messageSystem;
    private MsClient frontendMsClient;

    @Test
    @Disabled
    void test1() throws Exception {
        createMessageSystem(true);
        int counter = 1;
        CountDownLatch waitLatch = new CountDownLatch(counter);
        Message outMsg = frontendMsClient.produceMessage(DATABASE_SERVICE_CLIENT_NAME, new Box("AAA"),
                MessageType.USER_DATA, new MessageCallback<Box>() {
                    @Override
                    public void accept(Box box) {
                        assertEquals("AAA AAA", box.getText());
                        waitLatch.countDown();
                    }
                });
        frontendMsClient.sendMessage(outMsg);
        assertTrue(waitLatch.await(5, TimeUnit.SECONDS));
        messageSystem.dispose();
    }

    private void createMessageSystem(boolean startProcessing) throws Exception {
        log.info("setup");
        messageSystem = new MessageSystemImpl(startProcessing);
        CallbackRegistry callbackRegistry = new CallbackRegistryImpl();

        HandlersStore requestHandlerDatabaseStore = new HandlersStoreImpl();
        requestHandlerDatabaseStore.addHandler(MessageType.USER_DATA, new RequestHandler<ResultDataType>() {
            @Override
            public Optional<Message> handle(Message msg) {
                Box box = MessageHelper.getPayload(msg);
                return Optional.of(MessageBuilder.buildReplyMessage(msg, new Box(box.getText() + " " + box.getText())));
            }
        });
        MsClient databaseMsClient = new MsClientImpl(DATABASE_SERVICE_CLIENT_NAME, messageSystem,
                requestHandlerDatabaseStore, callbackRegistry);
        messageSystem.addClient(databaseMsClient);

        //////////////////////////
        HandlersStore requestHandlerFrontendStore = new HandlersStoreImpl();
        requestHandlerFrontendStore.addHandler(MessageType.USER_DATA, new RequestHandler<ResultDataType>() {
            @Override
            public Optional<Message> handle(Message msg) {
                log.info("new message:{}", msg);
                try {
                    MessageCallback<? extends ResultDataType> callback = callbackRegistry.getAndRemove(msg.getCallbackId());
                    if (callback != null) {
                        callback.accept(MessageHelper.getPayload(msg));
                    } else {
                        log.error("callback for Id:{} not found", msg.getCallbackId());
                    }
                } catch (Exception ex) {
                    log.error("msg:{}", msg, ex);
                }
                return Optional.empty();
            }
        });

        frontendMsClient = new MsClientImpl(FRONTEND_SERVICE_CLIENT_NAME, messageSystem,
                requestHandlerFrontendStore, callbackRegistry);
        messageSystem.addClient(frontendMsClient);

        log.info("setup done");
    }

    @Test
    void test2() throws Exception {
        messageSystem = new MessageSystemImpl(true);
        CallbackRegistry callbackRegistry = new CallbackRegistryImpl();


        MessageSystemRegisterByRmi messageSystemRmi = new MessageSystemRegisterByRmiServer(messageSystem, callbackRegistry);
        MessageSystemRegisterByRmi messageSystemRmiStub = (MessageSystemRegisterByRmi) UnicastRemoteObject.exportObject(messageSystemRmi, 0);
        Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);
        registry.bind("MessageSystemRegister", messageSystemRmiStub);

        MessageSystemRegisterByRmi clientMessageSystem = (MessageSystemRegisterByRmi) registry.lookup("MessageSystemRegister");

        HandlersStore requestHandlerDatabaseStore = new HandlersStoreImpl();
        requestHandlerDatabaseStore.addHandler(MessageType.USER_DATA, new RequestHandler<ResultDataType>() {
            @Override
            public Optional<Message> handle(Message msg) {
                Box box = MessageHelper.getPayload(msg);
                log.info("get box {}", box);
                return Optional.of(MessageBuilder.buildReplyMessage(msg, new Box(box.getText() + " " + box.getText())));
            }
        });

        MsClientByRmiClient databaseMsClient = new MsClientByRmiClient(DATABASE_SERVICE_CLIENT_NAME, clientMessageSystem, requestHandlerDatabaseStore, null);
        databaseMsClient.register();

        HandlersStoreImpl handler2 = new HandlersStoreImpl();
        handler2.addHandler(MessageType.USER_DATA, new CallbackRequestHandler<>(callbackRegistry));
        MsClientImpl frontendMsClient = new MsClientImpl(FRONTEND_SERVICE_CLIENT_NAME, messageSystem, handler2, callbackRegistry);
        messageSystem.addClient(frontendMsClient);

        int counter = 1;
        CountDownLatch waitLatch = new CountDownLatch(counter);
        Message outMsg = frontendMsClient.produceMessage(DATABASE_SERVICE_CLIENT_NAME, new Box("AAA"),
                MessageType.USER_DATA, new MessageCallback<Box>() {
                    @Override
                    public void accept(Box box) {
                        assertEquals("AAA AAA", box.getText());
                        waitLatch.countDown();
                    }
                });
        frontendMsClient.sendMessage(outMsg);
        assertTrue(waitLatch.await(5, TimeUnit.SECONDS));
        messageSystem.dispose();
    }
}
