package ru.otus.homework.herald.core;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Collection;

@Value
@Builder(toBuilder = true)
public class HeraldMeta {

    int access;
    String methodName;
    String methodDescriptor;
    String comment;
    boolean printProperty;

    @Singular
    Collection<LocalVariableMeta> localVariables;
}
