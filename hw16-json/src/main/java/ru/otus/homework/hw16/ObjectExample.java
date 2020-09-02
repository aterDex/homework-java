package ru.otus.homework.hw16;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.Collection;

@Getter
@Setter
@Builder
public class ObjectExample {

    private int i;
    private Long counter;
    private String name;
    private ObjectExampleInternal objectExampleInternal;
    @Singular
    private Collection<ObjectExampleInternal> collections;

    public ObjectExample() {
    }
}
