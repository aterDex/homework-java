insert into users (id, name, login, password)
values (nextval('users_sequence'), 'Администратор', 'admin', 'password');
insert into users (id, name, login, password)
values (nextval('users_sequence'), 'Пользователь', 'user', '123');
