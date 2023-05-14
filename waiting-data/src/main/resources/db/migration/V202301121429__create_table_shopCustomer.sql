create table cw_shop_customer
(
    shop_id            varchar(64)   not null comment '매장아이디',
    customer_seq       bigint        not null comment '통합고객 시퀀스',
    name               varchar(20)   null comment '고객명',
    memo               text          null comment '고객 메모',
    visit_count        int default 0 null comment '방문 횟수',
    cancel_count       int default 0 null comment '취소 횟수',
    last_visit_date    date          null comment '최근 방문일',
    reg_date_time      datetime(6)   not null comment '등록일자',
    mod_date_time      datetime(6)   not null comment '수정일자',
    constraint cw_shop_customer_pk
        primary key (shop_id, customer_seq)
)
    comment '매장별 고객 정보';

