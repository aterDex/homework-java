package ru.otus.homework.hw32.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.otus.messagesystem.client.ResultDataType;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserDto extends ResultDataType {

    private long id;

    private String name;

    private String login;

    private String password;
}
