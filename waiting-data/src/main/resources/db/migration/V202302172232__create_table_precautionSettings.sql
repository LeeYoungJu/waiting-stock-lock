create table cw_precaution_settings
(
    seq           bigint auto_increment comment '웨이팅 유의사항 시퀀스' primary key,
    shop_id       varchar(64)      not null comment '매장아이디',
    publish_yn    char default 'Y' not null comment '퍼블리시 여부',
    data          text             null comment '웨이팅 유의사항 설정',
    reg_date_time datetime(6)      not null comment '등록일자',
    mod_date_time datetime(6)      not null comment '수정일자'
)
    comment '웨이팅 유의사항 설정';

create index cw_precaution_settings_shop_id_index
    on cw_precaution_settings (shop_id);

