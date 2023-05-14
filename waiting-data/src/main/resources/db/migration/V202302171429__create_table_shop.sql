create table cw_shop
(
    seq        bigint auto_increment comment '웨이팅 매장 시퀀스'
        primary key,
    shop_id    varchar(64)      not null comment '웨이팅 매장 아이디',
    shop_name    varchar(80)      not null comment '웨이팅 매장 이름',
    shop_address    text    not null comment '웨이팅 매장 주소',
    shop_tel_number    varchar(20)      not null comment '웨이팅 매장 연락처',
    reg_date_time      datetime(6)      not null comment '등록일자',
    mod_date_time      datetime(6)      not null comment '수정일자'
)
    comment '웨이팅 매장 정보';

create index cw_shop_shop_id_index
    on cw_shop (shop_id);

