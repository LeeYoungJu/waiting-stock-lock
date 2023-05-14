alter table cw_shop_operation_info
    add remote_operation_start_date_time datetime(6) null comment '원격 운영 시작 일시' after operation_end_date_time;
alter table cw_shop_operation_info
    add remote_operation_end_date_time datetime(6) null comment '원격 운영 종료 일시' after remote_operation_start_date_time;
alter table cw_shop_operation_info
    add remote_auto_pause_start_date_time datetime(6) null comment '원격 자동 일시 정지 시작 시간' after auto_pause_reason;
alter table cw_shop_operation_info
    add remote_auto_pause_end_date_time datetime(6) null comment '원격 자동 일시 정지 종료 시간' after remote_auto_pause_start_date_time;

alter table cw_shop_operation_info_history
    add remote_operation_start_date_time datetime(6) null comment '원격 운영 시작 일시' after operation_end_date_time;
alter table cw_shop_operation_info_history
    add remote_operation_end_date_time datetime(6) null comment '원격 운영 종료 일시' after remote_operation_start_date_time;
alter table cw_shop_operation_info_history
    add remote_auto_pause_start_date_time datetime(6) null comment '원격 자동 일시 정지 시작 시간' after auto_pause_reason;
alter table cw_shop_operation_info_history
    add remote_auto_pause_end_date_time datetime(6) null comment '원격 자동 일시 정지 종료 시간' after remote_auto_pause_start_date_time;
