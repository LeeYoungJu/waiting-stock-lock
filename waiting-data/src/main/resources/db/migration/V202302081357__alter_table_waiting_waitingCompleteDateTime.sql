alter table cw_waiting
    change sitting_date_time waiting_complete_date_time datetime(6) null comment '웨이팅 완료(착석, 취소) 시간';


alter table cw_waiting_history
    change sitting_date_time waiting_complete_date_time datetime(6) null comment '웨이팅 완료(착석, 취소) 시간';

