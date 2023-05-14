alter table cw_menu
    change is_used_daily_stock used_daily_stock_yn char default 'N' not null comment '일별 재고 사용 여부';

alter table cw_menu
    change is_used_menu_quantity_per_team used_menu_quantity_per_team_yn char default 'N' not null comment '팀당 주문 가능 수량 사용 여부';

alter table cw_display_category
    change is_all_checked all_checked_yn char default 'N' not null comment '전체 메뉴 체크 여부';

alter table cw_order_line_item
    change menu_name menu_name varchar(100) not null comment '메뉴 이름';

alter table cw_order_line_item_history
    change menu_name menu_name varchar(100) not null comment '메뉴 이름';

alter table cw_display_menu
    add menu_name varchar(100) not null comment '메뉴 이름' after ordering;

alter table cw_display_menu
    add unit_price decimal(38, 2) not null comment '메뉴 단위 금액' after menu_name;
