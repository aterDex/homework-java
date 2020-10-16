package ru.otus.homework.herald.core.other;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class BoxForParameters {

    private String method;
    private Class<?>[] types;
    private Object[] data;
    private String result;
    private boolean target;
}
