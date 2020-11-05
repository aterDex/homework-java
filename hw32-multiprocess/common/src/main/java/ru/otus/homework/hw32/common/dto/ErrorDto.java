package ru.otus.homework.hw32.common.dto;

import lombok.Data;
import ru.otus.messagesystem.client.ResultDataType;

@Data
public class ErrorDto extends ResultDataType {

    private String text;
}
