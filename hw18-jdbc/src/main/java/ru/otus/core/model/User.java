package ru.otus.core.model;

import lombok.*;
import ru.otus.core.annotation.Id;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    private long id;
    private String name;
    private int age;
}
