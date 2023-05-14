create table cw_auto_cancel_target
(
    seq                       bigint auto_increment comment '웨이팅 자동취소 대상 시퀀스' primary key,
    shop_id                   varchar(64)      not null comment '매장아이디',
    waiting_id                varchar(64)      not null comment '웨이팅 아이디',
    expected_cancel_date_time datetime(6)      null comment '예상 착석 시간',
    publish_yn                char default 'Y' not null comment '퍼블리시 여부',
    data                      text             null comment '웨이팅 유의사항 설정',
    reg_date_time             datetime(6)      not null comment '등록일자',
    mod_date_time             datetime(6)      not null comment '수정일자'
)
    comment '웨이팅 유의사항 설정';

create index cw_auto_cancel_target_expected_cancel_date_time_index
    on cw_auto_cancel_target (expected_cancel_date_time);

