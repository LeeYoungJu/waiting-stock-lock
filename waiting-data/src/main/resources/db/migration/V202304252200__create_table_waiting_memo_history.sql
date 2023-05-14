create table cw_waiting_memo_history
(
    seq                 bigint auto_increment comment '웨이팅 메모 히스토리 시퀀스' primary key,
    waiting_memo_seq    bigint           not null comment '웨이팅 메모 시퀀스',
    shop_id             varchar(64)     not null comment '매장아이디',
    waiting_id          varchar(64)     not null comment '웨이팅 아이디',
    memo                varchar(2000)   not null comment '메모 내용',
    reg_date_time       datetime(6)      not null comment '등록일시'
)
comment '웨이팅 메모 히스토리';

create index cw_waiting_memo_history_waiting_memo_seq_index on cw_waiting_memo_history (waiting_memo_seq);
create index cw_waiting_memo_history_waiting_id_index on cw_waiting_memo_history (waiting_id);
