package ru.otus.homework.hw32.common.rmi;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.messagesystem.*;
import ru.otus.messagesystem.client.*;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageBuilder;
import ru.otus.messagesystem.message.MessageHelper;
import ru.otus.messagesystem.message.MessageType;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TtTest {

    private static final Logger log = LoggerFactory.getLogger(MsClientByRmiServer.class);

    private static final int REGISTRY_PORT = 1099;
    private static final int SERVER_PORT = 8090;

    private static final String FRONTEND_SERVICE_CLIENT_NAME = "frontendService";
    private static final String DATABASE_SERVICE_CLIENT_NAME = "databaseService";

    private MessageSystem messageSystem;
    private MsClient frontendMsClient;

    @Test
    @Disabled
    void test() throws Exception {
        try {
            TestCH tt = new TestCHImpl(SERVER_PORT);
            LocateRegistry.createRegistry(REGISTRY_PORT);
            Naming.rebind("//localhost/TechCH", tt);

            TestCH echoInterface = (TestCH) Naming.lookup("//localhost/TechCH");
            var dataFromServer = echoInterface.aa("YYYY ");
            assertEquals("YYYY AA", dataFromServer);
        } finally {
            Naming.unbind("//localhost/TechCH");
        }
    }

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
        // Создали часть которая относится к серверу
        messageSystem = new MessageSystemImpl(true);
        CallbackRegistry callbackRegistry = new CallbackRegistryImpl();

        // Создаем часть которая относится к серверу RMI

        MessageSystemRegisterByRmi messageSystemRmi = new MessageSystemRegisterByRmiServer(SERVER_PORT, REGISTRY_PORT, messageSystem);
        LocateRegistry.createRegistry(REGISTRY_PORT);
        Naming.rebind("//localhost/MessageSystemRegister", messageSystemRmi);

        MessageSystemRegisterByRmi clientMessageSystem = (MessageSystemRegisterByRmi) Naming.lookup("//localhost/MessageSystemRegister");


        HandlersStore requestHandlerDatabaseStore = new HandlersStoreImpl();
        requestHandlerDatabaseStore.addHandler(MessageType.USER_DATA, new RequestHandler<ResultDataType>() {
            @Override
            public Optional<Message> handle(Message msg) {
                Box box = MessageHelper.getPayload(msg);
                log.info("======= {} =======", box);
                return Optional.of(MessageBuilder.buildReplyMessage(msg, new Box(box.getText() + " " + box.getText())));
            }
        });

        MsClientByRmiClient databaseMsClient = new MsClientByRmiClient(DATABASE_SERVICE_CLIENT_NAME, REGISTRY_PORT, SERVER_PORT, requestHandlerDatabaseStore, null);

        RmiConnectInfo senderInfo = clientMessageSystem.addClient(databaseMsClient.getName(), databaseMsClient.getRmi());
        databaseMsClient.initSender(senderInfo);

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

        MsClientByRmiClient frontendMsClient = new MsClientByRmiClient(FRONTEND_SERVICE_CLIENT_NAME, REGISTRY_PORT, SERVER_PORT, requestHandlerFrontendStore, callbackRegistry);

        RmiConnectInfo senderInfo2 = clientMessageSystem.addClient(frontendMsClient.getName(), frontendMsClient.getRmi());
        frontendMsClient.initSender(senderInfo2);

        log.info("setup done");

        int counter = 1;
        CountDownLatch waitLatch = new CountDownLatch(counter);
        Message outMsg = frontendMsClient.produceMessage(DATABASE_SERVICE_CLIENT_NAME, new Box("AAA"),
                MessageType.USER_DATA, new MessageCallback<Box>() {
                    @Override
                    public void accept(Box box) {
                        log.warn("--- {} ---", box);
                        assertEquals("AAA AAA", box.getText());
                        waitLatch.countDown();
                    }
                });
        frontendMsClient.sendMessage(outMsg);
        assertTrue(waitLatch.await(5, TimeUnit.SECONDS));
        messageSystem.dispose();
    }
}
