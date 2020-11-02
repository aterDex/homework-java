package ru.otus.homework.hw31.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.otus.messagesystem.client.ResultDataType;

@Getter
@Setter
@AllArgsConstructor
public class ErrorAction extends ResultDataType {

    private String error;
}
