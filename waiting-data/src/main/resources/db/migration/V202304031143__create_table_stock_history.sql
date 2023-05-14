create table cw_stock_history
(
    seq                 bigint auto_increment comment '재고 히스토리 시퀀스' primary key,
    stock_seq           bigint           not null comment '재고 시퀀스',
    menu_id             varchar(64)      not null comment '메뉴 아이디',
    operation_date      date             not null comment '운영일',
    used_daily_stock_yn char default 'N' not null comment '일별 재고 사용 여부',
    stock               int              not null comment '재고 수량',
    sales_quantity      int              not null comment '판매 수량',
    out_of_stock_yn     char default 'N' not null comment '품절 여부',
    reg_date_time       datetime(6)      not null comment '등록일시'
)
    comment '재고 히스토리';

create index cw_stock_history_stock_seq_index on cw_stock_history (stock_seq);
create index cw_stock_history_menu_id_operation_date_index on cw_stock_history (menu_id, operation_date);
