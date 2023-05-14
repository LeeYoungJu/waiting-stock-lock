create table cw_customer
(
    seq                bigint auto_increment comment '고객 시퀀스'
        primary key,
    shop_id            varchar(64)   not null comment '매장아이디',
    enc_customer_phone varchar(80)   null comment '연락처',
    name               varchar(20)   null comment '고객명',
    memo               text          null comment '고객 메모',
    visit_count        int default 0 null comment '방문 횟수',
    cancel_count       int default 0 null comment '취소 횟수',
    last_visit_date    date          null comment '최근 방문일',
    reg_date_time      datetime(6)   not null comment '등록일자',
    mod_date_time      datetime(6)   not null comment '수정일자'
)
    comment '매장별 웨이팅 고객 정보';

create index cw_customer_shop_id_enc_customer_phone_index
    on cw_customer (shop_id, enc_customer_phone);