create table cw_category
(
    seq           bigint auto_increment comment '카테고리 시퀀스' primary key,
    category_id   varchar(64)  not null comment '카테고리 아이디',
    shop_id       varchar(64)  not null comment '매장 아이디',
    name          varchar(100) not null comment '카테고리 이름',
    ordering      int          not null comment '정렬 순서',
    reg_date_time datetime(6)  not null comment '등록일자',
    mod_date_time datetime(6)  not null comment '수정일자'
)
    comment '카테고리';

create index cw_category_category_id_index on cw_category (category_id);
create index cw_category_shop_id_index on cw_category (shop_id);

create table cw_menu
(
    seq                            bigint auto_increment comment '메뉴 시퀀스' primary key,
    menu_id                        varchar(64)      not null comment '메뉴 아이디',
    shop_id                        varchar(64)      not null comment '매장 아이디',
    name                           varchar(100)     not null comment '메뉴 이름',
    ordering                       int              not null comment '정렬 순서',
    unit_price                     decimal(38, 2)   not null comment '단위 가격',
    is_used_daily_stock            char default 'N' not null comment '일별 재고 사용 여부',
    daily_stock                    int              null comment '일별 재고',
    is_used_menu_quantity_per_team char default 'N' not null comment '팀당 주문 가능 수량 사용 여부',
    menu_quantity_per_team         int              null comment '팀 당 주문 가능 수량',
    reg_date_time                  datetime(6)      not null comment '등록일자',
    mod_date_time                  datetime(6)      not null comment '수정일자'
)
    comment '메뉴';

create index cw_menu_menu_id_index on cw_menu (menu_id);
create index cw_menu_shop_id_index on cw_menu (shop_id);

create table cw_category_menu
(
    seq           bigint auto_increment comment '카테고리메뉴 시퀀스' primary key,
    category_id   varchar(64) not null comment '카테고리 아이디',
    menu_id       varchar(64) not null comment '메뉴 아이디',
    reg_date_time datetime(6) not null comment '등록일자',
    mod_date_time datetime(6) not null comment '수정일자'
)
    comment '메뉴-카테고리 중간 매핑 테이블';

create index cw_category_menu_category_id_index on cw_category_menu (category_id);
create index cw_category_menu_menu_id_index on cw_category_menu (menu_id);

create table cw_display_category
(
    seq                  bigint auto_increment comment '노출 매핑 카테고리 시퀀스' primary key,
    category_id          varchar(64)      not null comment '카테고리 아이디',
    shop_id              varchar(64)      not null comment '매장 아이디',
    display_mapping_type varchar(30)      not null comment '노출 매핑 타입',
    ordering             int              not null comment '정렬 순서',
    is_all_checked       char default 'N' not null comment '전체 메뉴 체크 여부',
    reg_date_time        datetime(6)      not null comment '등록일자',
    mod_date_time        datetime(6)      not null comment '수정일자'
)
    comment '메뉴판 노출을 위한 매핑 카테고리';

create index cw_display_category_category_id_index on cw_display_category (category_id);
create index cw_display_category_shop_id_index on cw_display_category (shop_id);

create table cw_display_menu
(
    seq                  bigint auto_increment comment '노출 매핑 메뉴 시퀀스' primary key,
    category_id          varchar(64) not null comment '카테고리 아이디',
    menu_id              varchar(64) not null comment '메뉴 아이디',
    shop_id              varchar(64) not null comment '매장 아이디',
    display_mapping_type varchar(30) not null comment '노출 매핑 타입',
    ordering             int         not null comment '정렬 순서',
    reg_date_time        datetime(6) not null comment '등록일자',
    mod_date_time        datetime(6) not null comment '수정일자'
)
    comment '메뉴판 노출을 위한 매핑 메뉴';

create index cw_display_menu_category_id_index on cw_display_menu (category_id);
create index cw_display_menu_menu_id_index on cw_display_menu (menu_id);

create table cw_order
(
    seq            bigint auto_increment comment '주문 시퀀스' primary key,
    order_id       varchar(64)    not null comment '주문 아이디',
    shop_id        varchar(64)    not null comment '매장 아이디',
    waiting_id     varchar(64)    not null comment '웨이팅 아이디',
    operation_date date           not null comment '운영일',
    order_status   varchar(30)    not null comment '주문 상태',
    total_price    decimal(38, 2) not null comment '주문 총 금액',
    reg_date_time  datetime(6)    not null comment '등록일자',
    mod_date_time  datetime(6)    not null comment '수정일자'
)
    comment '웨이팅 주문';

create index cw_order_order_id_index on cw_order (order_id);
create index cw_order_shop_id_index on cw_order (shop_id);
create index cw_order_waiting_id_index on cw_order (waiting_id);

create table cw_order_line_item
(
    seq                    bigint auto_increment comment '주문 라인 아이템 시퀀스' primary key,
    order_id               varchar(64)    not null comment '주문 아이디',
    menu_id                varchar(64)    not null comment '메뉴 아이디',
    menu_name              varchar(64)    not null comment '메뉴 이름',
    order_line_item_status varchar(30)    not null comment '주문 라인 아이템 상태',
    unit_price             decimal(38, 2) not null comment '메뉴 단위 금액',
    line_price             decimal(38, 2) not null comment '메뉴 라인 금액',
    quantity               int            not null comment '수량',
    reg_date_time          datetime(6)    not null comment '등록일자',
    mod_date_time          datetime(6)    not null comment '수정일자'
)
    comment '주문 라인 아이템';

create index cw_order_line_item_order_id_index on cw_order_line_item (order_id);
create index cw_order_line_item_menu_id_index on cw_order_line_item (menu_id);

create table cw_order_history
(
    seq            bigint auto_increment comment '주문 히스토리 시퀀스' primary key,
    order_seq      bigint         not null comment '주문 시퀀스',
    order_id       varchar(64)    not null comment '주문 아이디',
    shop_id        varchar(64)    not null comment '매장 아이디',
    waiting_id     varchar(64)    not null comment '웨이팅 아이디',
    operation_date date           not null comment '운영일',
    order_status   varchar(30)    not null comment '주문 상태',
    total_price    decimal(38, 2) not null comment '주문 총 금액',
    reg_date_time  datetime(6)    not null comment '등록일자',
    mod_date_time  datetime(6)    not null comment '수정일자'
)
    comment '웨이팅 주문 히스토리';

create index cw_order_history_order_seq_index on cw_order_history (order_seq);
create index cw_order_history_order_id_index on cw_order_history (order_id);
create index cw_order_history_shop_id_index on cw_order_history (shop_id);
create index cw_order_history_waiting_id_index on cw_order_history (waiting_id);

create table cw_order_line_item_history
(
    seq                    bigint auto_increment comment '주문 라인 아이템 히스토리 시퀀스' primary key,
    order_line_item_seq    varchar(64)    not null comment '주문 라인 아이템 시퀀스',
    order_id               varchar(64)    not null comment '주문 아이디',
    menu_id                varchar(64)    not null comment '메뉴 아이디',
    menu_name              varchar(64)    not null comment '메뉴 이름',
    order_line_item_status varchar(30)    not null comment '주문 라인 아이템 상태',
    unit_price             decimal(38, 2) not null comment '메뉴 단위 금액',
    line_price             decimal(38, 2) not null comment '메뉴 라인 금액',
    quantity               int            not null comment '수량',
    reg_date_time          datetime(6)    not null comment '등록일자',
    mod_date_time          datetime(6)    not null comment '수정일자'
)
    comment '주문 라인 아이템 히스토리';

create index cw_order_line_item_history_order_line_item_seq_index on cw_order_line_item_history (order_line_item_seq);
create index cw_order_line_item_history_order_id_index on cw_order_line_item_history (order_id);
create index cw_order_line_item_history_menu_id_index on cw_order_line_item_history (menu_id);

create table cw_stock
(
    seq             bigint auto_increment comment '재고 시퀀스' primary key,
    menu_id         varchar(64) not null comment '메뉴 아이디',
    operation_date  date        not null comment '운영일',
    stock           int         not null comment '재고 수량',
    remaining_stock int         not null comment '남은 재고 수량',
    reg_date_time   datetime(6) not null comment '등록일자',
    mod_date_time   datetime(6) not null comment '수정일자'
)
    comment '재고';

create index cw_stock_menu_id_operation_date_index on cw_stock (menu_id, operation_date);
