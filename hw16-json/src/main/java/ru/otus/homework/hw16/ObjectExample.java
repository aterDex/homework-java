package ru.otus.homework.hw16;

import lombok.*;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectExample {

    private int i;
    private Long counter;
    private String name;
    private boolean tom;
    private ObjectExampleInternal objectExampleInternal;
    private Object uk;
    @Singular
    private Collection<ObjectExampleInternal> collections;
}
