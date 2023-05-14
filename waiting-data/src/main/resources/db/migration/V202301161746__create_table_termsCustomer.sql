
create table cw_terms_customer
(
    seq                bigint auto_increment comment '고객 약관 동의 시퀀스'
        primary key,
    shop_id            varchar(64)      not null comment '매장아이디',
    waiting_seq        bigint           not null comment '웨이팅 시퀀스',
    customer_seq       bigint           null comment '고객 시퀀스 (수기등록: null)',
    terms_seq          bigint           not null comment '약관 시퀀스',
    agree_yn          char default 'N' not null comment '동의여부',
    reg_date_time      datetime(6)      null comment '동의/철회 일시'
)
    comment '웨이팅 이용약관 고객 동의 내역';

create index cw_terms_customer_shop_id_index
    on cw_terms_customer (shop_id);

create index cw_terms_customer_customer_seq_index
    on cw_terms_customer (customer_seq);

create index cw_terms_customer_terms_seq_index
    on cw_terms_customer (terms_seq);

create index cw_terms_customer_waiting_seq_index
    on cw_terms_customer (waiting_seq);