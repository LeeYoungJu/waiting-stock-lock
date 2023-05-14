alter table cw_waiting
    add register_channel varchar(30) null comment '등록 타입 -- WAITING_APP: 현장, WAITING_MANAGER: 수기, CATCH_APP: 원격' after operation_date;

alter table cw_waiting_history
    add register_channel varchar(30) null comment '등록 타입 -- WAITING_APP: 현장, WAITING_MANAGER: 수기, CATCH_APP: 원격' after operation_date;

