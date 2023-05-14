create table cw_waiting
(
    seq  bigint auto_increment comment '매장 운영 정보 시퀀스'
        primary key,
    shop_id varchar(64) not null comment '매장아이디',
    operation_date date not null comment '영업일',
    customer_seq bigint not null comment '고객시퀀스',
    waiting_number int not null comment '웨이팅 채번',
    waiting_order int not null comment '웨이팅 순번',
    reg_waiting_order int not null comment '웨이팅 등록시점 순번',
    waiting_status varchar(10) null comment '웨이팅 상태 -- WAITING: 웨이팅중, SITTING: 착석, CANCEL: 취소',
    waiting_detail_status varchar(10) null comment '웨이팅 상태 상세',
    remark varchar(50) null comment '특이사항',
    table_mode_seats smallint null comment '테이블모드 N인석',
    seat_option_name varchar(10) null comment '좌석옵션명',
    total_seat_count smallint null comment '총 착석인원',
    person_options text null comment '인원 정보',
    expected_sitting_date_time datetime(6) null comment '예상 착석 시간',
    sitting_date_time datetime(6) null comment '착석 시간',
    reg_date_time               datetime(6)     not null comment '등록일자',
    mod_date_time               datetime(6)     not null comment '수정일자'
)
    comment '웨이팅';

create index cw_waiting_shop_id_index
    on cw_waiting (shop_id);
create index cw_waiting_operation_date_index
    on cw_waiting (operation_date);
create index cw_waiting_customer_seq_index
    on cw_waiting (customer_seq);
