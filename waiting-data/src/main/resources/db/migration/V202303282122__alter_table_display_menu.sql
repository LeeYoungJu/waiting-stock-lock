alter table cw_display_menu
    add checked_yn char default 'N' not null comment '체크 여부' after unit_price;
