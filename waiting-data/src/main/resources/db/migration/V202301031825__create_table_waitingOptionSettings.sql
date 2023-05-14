create table cw_option_settings
(
    seq  bigint auto_increment comment '옵션 설정 시퀀스'
        primary key,
    shop_id             varchar(64)      not null,
    publish_yn          char default 'N' not null comment '퍼블리시 여부',
    data                text             null     comment '옵션 설정 데이터',
    reg_date_time       datetime(6)      not null comment '등록일자',
    mod_date_time       datetime(6)      not null comment '수정일자'
)
comment '옵션 설정';

create index cw_option_settings_shop_id_index
    on cw_option_settings (shop_id);