package ru.otus.homework.hw32.common.dto;

import lombok.Data;
import ru.otus.messagesystem.client.ResultDataType;

@Data
public class UserDto extends ResultDataType {

    private long id;

    private String name;

    private String login;

    private String password;
}
