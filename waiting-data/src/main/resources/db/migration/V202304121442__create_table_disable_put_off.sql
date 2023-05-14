create table cw_disable_put_off
(
    seq           bigint auto_increment comment '미루기 off 시퀀스' primary key,
    shop_id       varchar(64) not null comment '매장 아이디',
    reg_date_time datetime(6) not null comment '등록일자',
    mod_date_time datetime(6) not null comment '수정일자'
)
    comment '미루기 off 매장';

create index cw_disable_put_off_shop_id_index on cw_disable_put_off (shop_id);
