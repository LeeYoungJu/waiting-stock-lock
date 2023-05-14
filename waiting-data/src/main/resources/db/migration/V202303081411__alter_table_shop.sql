alter table cw_shop
    add membership_yn char(1) default 'Y' not null comment '가맹 여부' after test_yn;
