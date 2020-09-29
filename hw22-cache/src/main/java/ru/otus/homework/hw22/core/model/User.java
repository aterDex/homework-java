package ru.otus.homework.hw22.core.model;

import lombok.*;
import ru.otus.homework.hw22.core.annotation.Id;

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
