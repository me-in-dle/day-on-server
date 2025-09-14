create table recommend_card
(
    id                       bigint      not null auto_increment,
    account_id               BIGINT      not null,
    contents                 TEXT,
    created_at               datetime(6) not null,
    created_id               VARCHAR(30) not null,
    daily_id                 BIGINT      not null,
    end_time_slot            datetime(6) not null,
    place                    VARCHAR(100),
    place_l_type             VARCHAR(255),
    place_s_type             VARCHAR(255),
    place_s_type_korean_name VARCHAR(255),
    start_time_slot          datetime(6) not null,
    status                   VARCHAR(30) not null,
    updated_at               datetime(6) not null,
    updated_id               VARCHAR(30) not null,
    primary key (id)
) engine = InnoDB;

create index idx_recommend_card_01
    on recommend_card (account_id);

create index idx_recommend_card_02
    on recommend_card (daily_id);

create table recommend_feedback
(
    id                       bigint       not null auto_increment,
    account_id               BIGINT       not null,
    created_at               datetime(6)  not null,
    created_id               VARCHAR(30)  not null,
    feedback_actions         VARCHAR(255) not null,
    last_matched_count       datetime(6),
    last_mismatched_count    datetime(6),
    matched_count            BIGINT       not null,
    mismatched_count         BIGINT       not null,
    place                    VARCHAR(100),
    place_l_type             VARCHAR(255),
    place_s_type             VARCHAR(255),
    place_s_type_korean_name VARCHAR(255),
    time_slot                VARCHAR(30)  not null,
    updated_at               datetime(6)  not null,
    updated_id               VARCHAR(30)  not null,
    primary key (id)
) engine = InnoDB;
create index idx_recommend_feedback_01
    on recommend_feedback (account_id);
create index idx_recommend_feedback_02
    on recommend_feedback (account_id, place_l_type, place_s_type);