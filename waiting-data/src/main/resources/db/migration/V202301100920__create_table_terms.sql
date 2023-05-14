create table cw_terms
(
    seq         bigint auto_increment comment '이용약관 시퀀스'
        primary key,
    terms_code        varchar(100)     not null comment '약관 코드',
    terms_subject     varchar(180)     not null comment '약관 제목',
    terms_content     longtext         null comment '약관 내용',
    terms_version     varchar(50)      not null comment '약관 버전(날짜)',
    terms_url         varchar(200)     null comment '약관 주소',
    terms_order       int              not null comment '약관 정렬',
    use_yn            char default 'N' not null comment '사용 여부',
    required_yn       char default 'N' not null comment '필수 여부',
    marketing_yn      char default 'N' not null comment '마케팅동의약관 여부',
    publish_date_time datetime(6)      null comment '약관 발표 일시',
    apply_date_time   datetime(6)      null comment '약관 적용 일시',
    reg_date_time     datetime(6)      null comment '등록일자',
    mod_date_time     datetime(6)      null comment '수정일자'
)
    comment '웨이팅 이용약관';