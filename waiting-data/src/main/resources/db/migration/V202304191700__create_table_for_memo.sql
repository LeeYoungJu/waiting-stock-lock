create table cw_memo_keyword
(
    seq                 bigint auto_increment comment '매장별 키워드 설정 시퀀스' primary key,
    keyword_id          varchar(64)      not null comment '키워드 아이디',
    shop_id             varchar(64)      not null comment '매장아이디',
    keyword             varchar(20)      not null comment '키워드',
    ordering            int              not null comment '정렬 순서',
    deleted_yn          char default 'N' not null comment '삭제 여부',
    reg_date_time       datetime(6)      not null comment '등록일자',
    mod_date_time       datetime(6)      not null comment '수정일자'
)
comment '메모 키워드 설정';

create index cw_memo_keyword_shop_id_index
    on cw_memo_keyword (shop_id);
create index cw_memo_keyword_keyword_id_index
    on cw_memo_keyword (keyword_id);


create table cw_waiting_memo
(
    seq                 bigint auto_increment comment '웨이팅장별 메모 시퀀스' primary key,
    shop_id             varchar(64)     not null comment '매장아이디',
    waiting_id          varchar(64)     not null comment '웨이팅 아이디',
    memo                varchar(2000)   not null comment '메모 내용',
    reg_date_time       datetime(6)      not null comment '등록일자',
    mod_date_time       datetime(6)      not null comment '수정일자'
)
comment '웨이팅별 메모';

create index cw_waiting_memo_waiting_id_index
    on cw_waiting_memo (waiting_id);

