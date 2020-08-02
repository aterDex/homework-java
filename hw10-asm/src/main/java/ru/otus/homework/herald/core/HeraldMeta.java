package ru.otus.homework.herald.core;

import lombok.Builder;
import lombok.Value;
import lombok.Singular;

import java.util.Collection;

@Value
@Builder(toBuilder = true)
public class HeraldMeta {

    int access;
    String methodName;
    String methodDescriptor;
    @Singular
    Collection<LocalVariableMeta> localVariables;
}
