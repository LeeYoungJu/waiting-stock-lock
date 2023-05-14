create table cw_notice
(
    seq              bigint auto_increment comment '공지사항 시퀀스' primary key,
    notice_type      varchar(20)  not null comment '공지 유형 (NORMAL, UPDATE)',
    notice_always_yn char(1)      null comment '항상 게시 여부',
    enable_yn        char(1)      null comment '게시 여부',
    open_date_time   datetime(6)  null comment '게시 시작 일시',
    close_date_time  datetime(6)  null comment '게시 종료 일시',
    notice_title     varchar(50)  null comment '공지 제목',
    notice_preview   varchar(100) null comment '공지 한줄 미리보기',
    notice_content   MEDIUMTEXT   null comment '공지 내용',
    reg_date_time    datetime(6)  null comment '생성 일시',
    mod_date_time    datetime(6)  null comment '수정 일시'
)
    comment '매장 노출용 메인 공지사항';

create index cw_notice_open_date_time_index
    on cw_notice (open_date_time);

create index cw_notice_close_date_time_index
    on cw_notice (close_date_time);


create table cw_notice_read
(
    shop_id       varchar(64) comment '매장 ID',
    notice_seq    bigint comment '공지사항 시퀀스',
    reg_date_time datetime(6) null comment '생성 일시',
    primary key (shop_id, notice_seq)
)
    comment '매장 노출용 메인 공지사항 조회 기록';