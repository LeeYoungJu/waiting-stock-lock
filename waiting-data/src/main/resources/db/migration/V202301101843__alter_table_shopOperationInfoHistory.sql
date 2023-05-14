ALTER TABLE cw_shop_operation_info_history
    ADD shop_name varchar(80) not null  comment '매장 이름' after shop_id;