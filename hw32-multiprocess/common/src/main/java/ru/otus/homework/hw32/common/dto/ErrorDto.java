package ru.otus.homework.hw32.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.otus.messagesystem.client.ResultDataType;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ErrorDto extends ResultDataType {

    private String text;
}
