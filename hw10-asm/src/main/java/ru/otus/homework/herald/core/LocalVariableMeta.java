package ru.otus.homework.herald.core;

import lombok.Builder;
import lombok.Value;
import org.objectweb.asm.Label;

@Value
@Builder(toBuilder = true)
public class LocalVariableMeta {

    String name;
    String descriptor;
    String signature;
    int index;
}
