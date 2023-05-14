alter table cw_shop_customer
    add noshow_count int default 0 null comment '노쇼 횟수' after cancel_count;

alter table cw_shop_customer
    add sitting_count int default 0 null comment '착석 횟수' after noshow_count;

