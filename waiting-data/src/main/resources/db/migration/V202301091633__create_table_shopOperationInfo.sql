create table cw_shop_operation_info
(
    seq  bigint auto_increment comment '매장 운영 정보 시퀀스'
        primary key,
    shop_id                     varchar(64)     not null comment '매장아이디',
    operation_date              date            not null comment '영업일',
    operation_status            varchar(10)     not null comment '운영 상태 -- OPEN: 영업중, BY_PASS: 바로입장, PAUSE: 일시정지, CLOSED: 영업 종료',
    operation_start_date_time   datetime(6)     not null comment '운영 시작 시각',
    operation_end_date_time     datetime(6)     null comment '운영 종료 시각',
    pause_start_date_time       datetime(6)     null comment '일시 정지 시작 시간',
    pause_end_date_time         datetime(6)     null comment '일시 정지 종료 시간',
    pause_trigger               varchar(10)     null comment '일시 정지 트리거 -- AUTO, MANUAL',
    pause_memo                  varchar(50)     null comment '일시 정지 사유',
    reg_date_time               datetime(6)     not null comment '등록일자',
    mod_date_time               datetime(6)     not null comment '수정일자'
)
comment '매장 운영 정보';

create index cw_shop_operation_info_shop_id_index
    on cw_shop_operation_info (shop_id);
create index cw_shop_operation_info_operation_date_index
    on cw_shop_operation_info (operation_date);