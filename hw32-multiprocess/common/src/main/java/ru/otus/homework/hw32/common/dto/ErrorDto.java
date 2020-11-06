package ru.otus.homework.hw32.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.messagesystem.client.ResultDataType;

@Data
@AllArgsConstructor
public class ErrorDto extends ResultDataType {

    private String text;
}
