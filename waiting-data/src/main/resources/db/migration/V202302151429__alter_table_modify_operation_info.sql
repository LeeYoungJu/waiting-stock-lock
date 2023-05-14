
alter table cw_shop_operation_info
    modify closed_reason varchar(30) null comment '영업 종료 사유';

alter table cw_shop_operation_info_history
    modify closed_reason varchar(30) null comment '영업 종료 사유';