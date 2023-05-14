create table cw_order_settings
(
    seq           bigint auto_increment comment '주문 설정 시퀀스' primary key,
    shop_id       varchar(64)      not null comment '매장 아이디',
    publish_yn    char default 'N' not null comment '사용 여부',
    data          text             null comment '주문 설정',
    reg_date_time datetime(6)      not null comment '등록일자',
    mod_date_time datetime(6)      not null comment '수정일자'
)
    comment '주문 설정';

create index cw_order_settings_shop_id_index on cw_order_settings (shop_id);
