alter table cw_shop
    add remote_waiting_use_yn char(1) default 'Y' not null comment '원격 웨이팅 사용 여부' after shop_tel_number;