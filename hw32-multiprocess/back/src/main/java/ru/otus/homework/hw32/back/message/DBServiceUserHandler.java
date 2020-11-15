package ru.otus.homework.hw32.back.message;

import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.back.data.core.model.User;
import ru.otus.homework.hw32.back.data.core.service.DBServiceUser;
import ru.otus.homework.hw32.common.dto.ErrorDto;
import ru.otus.homework.hw32.common.dto.UserCollectionDto;
import ru.otus.homework.hw32.common.dto.UserDto;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageBuilder;
import ru.otus.messagesystem.message.MessageHelper;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class DBServiceUserHandler implements RequestHandler<UserDto> {

    private final DBServiceUser dbService;

    public DBServiceUserHandler(DBServiceUser dbService) {
        this.dbService = dbService;
    }

    @Override
    public Optional<Message> handle(Message msg) {
        try {
            Object payload = MessageHelper.getPayload(msg);
            if (payload instanceof UserDto) {
                return handle(msg, (UserDto) payload);
            } else if (payload instanceof UserCollectionDto) {
                return handle(msg, (UserCollectionDto) payload);
            }
            log.warn("Unknown message {}", msg);
            return Optional.empty();
        } catch (Exception e) {
            log.error("", e);
            return Optional.of(MessageBuilder.buildReplyMessage(msg, new ErrorDto(String.format("Ошибка при обработки команды %s. %s", msg, e.getMessage()))));
        }
    }

    private Optional<Message> handle(Message msg, UserCollectionDto payload) {
        payload.setUsers(dbService.getUsers().stream().map(this::convert).collect(Collectors.toList()));
        return Optional.of(MessageBuilder.buildReplyMessage(msg, payload));
    }

    private Optional<Message> handle(Message msg, UserDto payload) {
        User user = convert(payload);
        dbService.saveUser(user);
        return Optional.of(MessageBuilder.buildReplyMessage(msg, convert(user)));
    }

    private UserDto convert(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        dto.setPassword(user.getPassword());
        return dto;
    }

    private User convert(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setLogin(dto.getLogin());
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        return user;
    }
}
