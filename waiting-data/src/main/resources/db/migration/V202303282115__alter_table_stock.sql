alter table cw_stock
    add used_daily_stock_yn char default 'N' not null comment '일별 재고 사용 여부' after operation_date;

alter table cw_stock
    change remaining_stock sales_quantity int not null comment '판매 수량';
