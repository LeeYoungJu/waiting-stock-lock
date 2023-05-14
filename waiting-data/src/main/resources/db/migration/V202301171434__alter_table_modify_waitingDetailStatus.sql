alter table cw_waiting
    modify waiting_detail_status varchar(30) null comment '웨이팅 상태 상세';

alter table cw_waiting_history
    modify waiting_detail_status varchar(30) null comment '웨이팅 상태 상세';

