package ru.otus.homework.data.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users", indexes = {
        @Index(name = "IDX_USERS_LOGIN", columnList = "login", unique = true)
})
@NamedQueries({
        @NamedQuery(name = "get_user_by_login", query = "select u from User u where login = :login"),
        @NamedQuery(name = "get_all_users", query = "select u from User u")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;
}
