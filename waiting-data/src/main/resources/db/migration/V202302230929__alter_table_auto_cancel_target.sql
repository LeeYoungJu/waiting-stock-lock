drop table cw_auto_cancel_target;

create table cw_auto_cancel_target
(
    seq                       bigint auto_increment comment '웨이팅 자동취소 대상 시퀀스' primary key,
    shop_id                   varchar(64)      not null comment '매장아이디',
    waiting_id                varchar(64)      not null comment '웨이팅 아이디',
    expected_cancel_date_time datetime(6)      null comment '예상 취소 시간',
    processing_status varchar(30) not null comment '처리 여부 -- CREATED: 신규 생성, SUCCESS: 성공, FAIL: 실패',
    reg_date_time             datetime(6)      not null comment '등록일자',
    mod_date_time             datetime(6)      not null comment '수정일자'
)
    comment '자동 취소 대상';

create index cw_auto_cancel_target_status_expected_date_time_index
    on cw_auto_cancel_target (processing_status, expected_cancel_date_time);

create unique index uidx_auto_cancel_target_waiting_id on cw_auto_cancel_target (waiting_id);
