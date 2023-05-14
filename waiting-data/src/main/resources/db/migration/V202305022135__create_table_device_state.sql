create table cw_device_state
(
    device_uuid      varchar(64) not null comment '패드 UUID'
        primary key,
    shop_id          varchar(64) null comment '매장아이디',
    server_address   varchar(50) null comment '서버 IP 주소',
    connection_state varchar(16) null comment '연결 상태',
    reg_date_time      datetime(6)      not null comment '등록일자',
    mod_date_time      datetime(6)      not null comment '수정일자'
)
comment '패드 연결 상태 테이블';

create index cw_device_state_shop_id_index on cw_device_state (shop_id);

create table cw_device_state_history
(
    seq                 bigint auto_increment comment '패드 연결 상태 히스토리 시퀀스' primary key,
    device_uuid      varchar(64) not null comment '패드 UUID',
    shop_id          varchar(64) null comment '매장아이디',
    server_address   varchar(50) null comment '서버 IP 주소',
    connection_state varchar(16) null comment '연결 상태',
    reg_date_time      datetime(6)      not null comment '등록일자'
)
    comment '패드 연결 상태 히스토리 테이블';

create index cw_device_state_history_shop_id_index on cw_device_state_history (shop_id);
