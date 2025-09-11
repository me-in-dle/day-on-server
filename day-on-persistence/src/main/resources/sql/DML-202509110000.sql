create table accounts
(
    age        INT,
    created_at datetime(6) not null,
    id         bigint      not null auto_increment,
    updated_at datetime(6) not null,
    created_id VARCHAR(30) not null,
    nick_name  VARCHAR(50) not null,
    updated_id VARCHAR(30) not null,
    primary key (id)
) engine = InnoDB;

create table connect_account
(
    is_email_verified BOOLEAN DEFAULT FALSE not null,
    account_id        BIGINT                not null,
    created_at        datetime(6)           not null,
    id                bigint                not null auto_increment,
    updated_at        datetime(6)           not null,
    created_id        VARCHAR(30)           not null,
    email             VARCHAR(100)          not null,
    updated_id        VARCHAR(30)           not null,
    connect_type      VARCHAR(20)           not null,
    primary key (id)
) engine = InnoDB;

create index idx_connect_account_01
    on connect_account (account_id);

create index idx_connect_account_02
    on connect_account (email, connect_type);