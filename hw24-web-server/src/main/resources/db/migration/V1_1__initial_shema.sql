create sequence users_sequence start with 1 increment by 1;
create table users (id bigint not null, login varchar(255), name varchar(255), password varchar(255), primary key (id));
alter table users add constraint IDX_USERS_LOGIN unique (login);
