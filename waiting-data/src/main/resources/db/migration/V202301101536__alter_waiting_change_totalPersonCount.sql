alter table cw_waiting
    change total_seat_count total_person_count smallint null comment '총 입장 인원 (착석 + 비착석)';

alter table cw_waiting_history
    change total_seat_count total_person_count smallint null comment '총 입장 인원 (착석 + 비착석)';