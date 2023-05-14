alter table cw_shop_operation_info
    modify operation_start_date_time datetime(6) null comment '운영 시작 시각';
alter table cw_shop_operation_info_history
    modify operation_start_date_time datetime(6) null comment '운영 시작 시각';
