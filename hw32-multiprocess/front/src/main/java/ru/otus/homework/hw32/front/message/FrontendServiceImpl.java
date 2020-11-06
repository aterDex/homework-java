package ru.otus.homework.hw32.front.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.homework.hw32.common.dto.ErrorDto;
import ru.otus.homework.hw32.common.dto.UserCollectionDto;
import ru.otus.homework.hw32.common.dto.UserDto;
import ru.otus.messagesystem.client.MessageCallback;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageType;

@Slf4j
@Service
public class FrontendServiceImpl implements FrontendService {

    private final MsClient msClient;
    private final String databaseServiceClientName;

    public FrontendServiceImpl(MsClient msClient, @Value("${message-system.database-service-client-name}") String databaseServiceClientName) {
        this.msClient = msClient;
        this.databaseServiceClientName = databaseServiceClientName;
    }

    @Override
    public void saveUser(UserDto user, MessageCallback<UserDto> dataConsumer, MessageCallback<ErrorDto> errorConsumer) {
        Message outMsg = msClient.produceMessage(databaseServiceClientName, user,
                MessageType.USER_DATA, (MessageCallback<ResultDataType>) data -> {
                    if (data instanceof ErrorDto) {
                        errorConsumer.accept((ErrorDto) data);
                    } else if (data instanceof UserDto) {
                        dataConsumer.accept((UserDto) data);
                    } else {
                        log.error("Unknown data {}.", data);
                    }
                });
        msClient.sendMessage(outMsg);
    }

    @Override
    public void getAllUsers(MessageCallback<UserCollectionDto> dataConsumer, MessageCallback<ErrorDto> errorConsumer) {
        Message outMsg = msClient.produceMessage(databaseServiceClientName, new UserCollectionDto(),
                MessageType.USER_DATA, (MessageCallback<ResultDataType>) data -> {
                    if (data instanceof ErrorDto) {
                        errorConsumer.accept((ErrorDto) data);
                    } else if (data instanceof UserCollectionDto) {
                        dataConsumer.accept((UserCollectionDto) data);
                    } else {
                        log.error("Unknown data {}.", data);
                    }
                });
        msClient.sendMessage(outMsg);
    }
}
