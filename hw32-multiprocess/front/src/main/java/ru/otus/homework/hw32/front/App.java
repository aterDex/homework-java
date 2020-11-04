package ru.otus.homework.hw32.front;

import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.rmi.Box;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmi;
import ru.otus.homework.hw32.common.rmi.MsClientByRmiClient;
import ru.otus.homework.hw32.common.rmi.RmiConnectInfo;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.CallbackRegistryImpl;
import ru.otus.messagesystem.client.MessageCallback;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageHelper;
import ru.otus.messagesystem.message.MessageType;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

//@SpringBootApplication
@Slf4j
public class App {

    private static final int REGISTRY_PORT_DST = 13030;
    private static final int REGISTRY_PORT = 13050;
    private static final int SERVER_PORT = 13051;

    private static final String FRONTEND_SERVICE_CLIENT_NAME = "frontendService";
    private static final String DATABASE_SERVICE_CLIENT_NAME = "databaseService";

    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(REGISTRY_PORT);
        MessageSystemRegisterByRmi clientMessageSystem = (MessageSystemRegisterByRmi) Naming.lookup("//localhost:" + REGISTRY_PORT_DST + "/MessageSystemRegister");
        CallbackRegistry callbackRegistry = new CallbackRegistryImpl();

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
                        waitLatch.countDown();
                    }
                });
        frontendMsClient.sendMessage(outMsg);
        log.info("result {}", waitLatch.await(5, TimeUnit.SECONDS));
    }
//    public static void main(String[] args) {
//        SpringApplication.run(App.class, args);
//    }
}