alter table cw_shop_operation_info
    add manual_pause_start_date_time datetime(6) null comment '수동 일시 정지 시작 시간' after operation_end_date_time;
alter table cw_shop_operation_info
    add manual_pause_end_date_time datetime(6) null comment '수동 일시 정지 종료 시간' after manual_pause_start_date_time;
alter table cw_shop_operation_info
    add auto_pause_start_date_time datetime(6) null comment '자동 일시 정지 시작 시간' after manual_pause_end_date_time;
alter table cw_shop_operation_info
    add auto_pause_end_date_time datetime(6) null comment '자동 일시 정지 종료 시간' after auto_pause_start_date_time;
alter table cw_shop_operation_info
    add closed_reason datetime(6) null comment '자동 일시 정지 종료 시간' after auto_pause_end_date_time;

alter table cw_shop_operation_info
    drop pause_trigger;

alter table cw_shop_operation_info
    drop pause_memo;

alter table cw_shop_operation_info
    add manual_pause_reason varchar(50) null comment '수동 일시 정지 사유' after closed_reason;
alter table cw_shop_operation_info
    add auto_pause_reason varchar(50) null comment '자동 일시 정지 사유' after manual_pause_reason;

-- History
alter table cw_shop_operation_info_history
    add manual_pause_start_date_time datetime(6) null comment '수동 일시 정지 시작 시간' after operation_end_date_time;
alter table cw_shop_operation_info_history
    add manual_pause_end_date_time datetime(6) null comment '수동 일시 정지 종료 시간' after manual_pause_start_date_time;
alter table cw_shop_operation_info_history
    add auto_pause_start_date_time datetime(6) null comment '자동 일시 정지 시작 시간' after manual_pause_end_date_time;
alter table cw_shop_operation_info_history
    add auto_pause_end_date_time datetime(6) null comment '자동 일시 정지 종료 시간' after auto_pause_start_date_time;
alter table cw_shop_operation_info_history
    add closed_reason datetime(6) null comment '자동 일시 정지 종료 시간' after auto_pause_end_date_time;

alter table cw_shop_operation_info_history
    drop pause_trigger;

alter table cw_shop_operation_info_history
    drop pause_memo;

alter table cw_shop_operation_info_history
    add manual_pause_reason varchar(50) null comment '수동 일시 정지 사유' after closed_reason;
alter table cw_shop_operation_info_history
    add auto_pause_reason varchar(50) null comment '자동 일시 정지 사유' after manual_pause_reason;
