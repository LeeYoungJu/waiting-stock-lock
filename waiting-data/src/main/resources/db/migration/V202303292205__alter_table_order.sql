alter table cw_order
    add order_type varchar(20) not null comment '주문 종류' after operation_date;
