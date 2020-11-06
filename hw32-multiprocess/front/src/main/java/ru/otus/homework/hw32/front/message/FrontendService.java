package ru.otus.homework.hw32.front.message;

import ru.otus.homework.hw32.common.dto.ErrorDto;
import ru.otus.homework.hw32.common.dto.UserCollectionDto;
import ru.otus.homework.hw32.common.dto.UserDto;
import ru.otus.messagesystem.client.MessageCallback;

public interface FrontendService {

    void saveUser(UserDto user, MessageCallback<UserDto> dataConsumer, MessageCallback<ErrorDto> errorConsumer);

    void getAllUsers(MessageCallback<UserCollectionDto> dataConsumer, MessageCallback<ErrorDto> errorConsumer);
}
