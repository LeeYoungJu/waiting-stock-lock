alter table cw_disable_put_off
    add publish_yn char default 'N' not null comment '퍼블리시 여부' after shop_id;
