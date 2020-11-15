package ru.otus.homework.hw32.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.otus.messagesystem.client.ResultDataType;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserCollectionDto extends ResultDataType {

    private List<UserDto> users;
}
