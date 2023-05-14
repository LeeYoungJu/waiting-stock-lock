alter table cw_menu
    add deleted_yn char default 'N' not null comment '삭제 여부' after menu_quantity_per_team;

alter table cw_category
    add deleted_yn char default 'N' not null comment '삭제 여부' after ordering;
