alter table cw_waiting
    modify customer_seq bigint null comment '고객시퀀스';

alter table cw_waiting_history
    modify customer_seq bigint null comment '고객시퀀스';


