alter table cw_shop_operation_info
    drop pause_start_date_time;

alter table cw_shop_operation_info_history
    drop pause_end_date_time;

alter table cw_shop_operation_info
    change operation_status registrable_status varchar (10) not null comment '운영 상태 -- OPEN: 영업중, BY_PASS: 바로입장, CLOSED: 영업 종료';
