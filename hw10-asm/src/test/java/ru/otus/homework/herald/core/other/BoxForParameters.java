package ru.otus.homework.herald.core.other;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoxForParameters {

    private String method;
    private Class<?>[] types;
    private Object[] data;
    private String result;
    private boolean target;
}
