create table cw_remote_operation_time_settings
(
    seq                   bigint auto_increment comment '원격 운영시간 시퀀스' primary key,
    shop_id               varchar(64)      not null comment '매장 아이디',
    operation_day         varchar(20)      not null comment '운영 요일',
    operation_start_time  time(6)          not null comment '운영 시작 시각',
    operation_end_time    time(6)          not null comment '운영 종료 시각',
    closed_day_yn         char default 'N' not null comment '휴무일 여부',
    used_auto_pause_yn    char default 'N' not null comment '자동 일시정지 사용 여부',
    auto_pause_start_time time(6)          null comment '자동 일시정지 시작 시각',
    auto_pause_end_time   time(6)          null comment '자동 일시정지 종료 시각',
    publish_yn            char default 'N' not null comment '퍼블리시 여부',
    reg_date_time         datetime(6)      not null comment '등록일자',
    mod_date_time         datetime(6)      not null comment '수정일자'
)
    comment '매장 원격 운영시간 세팅';

create index cw_remote_operation_time_settings_shop_id_index on cw_remote_operation_time_settings (shop_id);
