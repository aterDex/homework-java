package ru.otus.homework.hw18.core.model;

import lombok.*;
import ru.otus.homework.hw18.core.annotation.Id;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private long id;
    private String name;
    private int age;
}
