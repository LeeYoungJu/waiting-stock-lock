alter table cw_message_send_history
    add waiting_id varchar(64) not null comment '웨이팅 아이디' after waiting_history_seq;
