
alter table cw_waiting_seat_canceled_history
    add canceled_waiting_team_count int not null comment '취소 전 순서'  after canceled_waiting_seq;



alter table cw_waiting_seat_canceled_history
    add canceled_waiting_expected_waiting_period int null comment '취소 전 남은 예상시간' after canceled_waiting_team_count;