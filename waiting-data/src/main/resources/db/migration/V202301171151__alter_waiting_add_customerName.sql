alter table cw_waiting
    add customer_name varchar(20) null comment '고객명' after customer_seq;

alter table cw_waiting_history
    add customer_name varchar(20) null comment '고객명' after customer_seq;

