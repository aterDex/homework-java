package ru.otus.homework.hw32.back;

import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.dto.UserDto;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmi;
import ru.otus.homework.hw32.common.rmi.MsClientByRmiClient;
import ru.otus.homework.hw32.common.rmi.RmiConnectInfo;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageBuilder;
import ru.otus.messagesystem.message.MessageHelper;
import ru.otus.messagesystem.message.MessageType;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Optional;

@Slf4j
public class App {

    private static final int REGISTRY_PORT_DST = 13030;
    private static final int REGISTRY_PORT = 13040;
    private static final int SERVER_PORT = 13041;

    private static final String FRONTEND_SERVICE_CLIENT_NAME = "frontendService";
    private static final String DATABASE_SERVICE_CLIENT_NAME = "databaseService";

    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(REGISTRY_PORT);
        MessageSystemRegisterByRmi clientMessageSystem = (MessageSystemRegisterByRmi) Naming.lookup("//localhost:" + REGISTRY_PORT_DST + "/MessageSystemRegister");

        HandlersStore requestHandlerDatabaseStore = new HandlersStoreImpl();
        requestHandlerDatabaseStore.addHandler(MessageType.USER_DATA, new RequestHandler<ResultDataType>() {
            @Override
            public Optional<Message> handle(Message msg) {
                UserDto user = MessageHelper.getPayload(msg);
                log.info("======= {} =======", user);
                return Optional.of(MessageBuilder.buildReplyMessage(msg, user));
            }
        });

        MsClientByRmiClient databaseMsClient = new MsClientByRmiClient(DATABASE_SERVICE_CLIENT_NAME, REGISTRY_PORT, SERVER_PORT, requestHandlerDatabaseStore, null);

        RmiConnectInfo senderInfo = clientMessageSystem.addClient(databaseMsClient.getName(), databaseMsClient.getRmi());
        databaseMsClient.initSender(senderInfo);
    }
}
