alter table cw_waiting
    add waiting_id varchar(64) not null comment '웨이팅 아이디' after shop_id;


alter table cw_waiting_history
    add waiting_id varchar(64) not null comment '웨이팅 아이디' after shop_id;


