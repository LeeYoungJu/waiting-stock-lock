alter table cw_stock
    add out_of_stock_yn char default 'N' not null comment '품절 여부' after sales_quantity;
