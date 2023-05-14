alter table cw_shop
    add test_yn char(1) not null comment '테스트 매장 여부' after remote_waiting_use_yn;
