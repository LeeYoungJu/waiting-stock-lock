create table cw_waiting_seat_canceled_history
(
    seq                  bigint auto_increment comment '시퀀스' primary key,
    seat_waiting_seq     bigint      not null comment '착석 웨이팅 시퀀스',
    canceled_waiting_seq bigint      not null comment '취소된 웨이팅 시퀀스',
    reg_date_time        datetime(6) not null comment '등록일자'
)
    comment '착석에 의한 웨이팅 취소 내역';



create index cw_waiting_seat_canceled_history_seat_waiting_seq_index
    on cw_waiting_seat_canceled_history (seat_waiting_seq);