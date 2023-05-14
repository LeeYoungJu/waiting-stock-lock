alter table cw_shop_operation_info
    add manual_pause_reason_id varchar(64) null comment '수동 일시 정지 사유 아이디' after closed_reason;
alter table cw_shop_operation_info
    add auto_pause_reason_id varchar(64) null comment '자동 일시 정지 사유 아이디' after manual_pause_reason;

alter table cw_shop_operation_info_history
    add manual_pause_reason_id varchar(64) null comment '수동 일시 정지 사유 아이디' after closed_reason;
alter table cw_shop_operation_info_history
    add auto_pause_reason_id varchar(64) null comment '자동 일시 정지 사유 아이디' after manual_pause_reason;