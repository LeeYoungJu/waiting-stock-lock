create table cw_message_sms_template
(
    seq  bigint auto_increment comment '문자메시지 템플릿 시퀀스'
        primary key,
    template_name               varchar(50)     not null    comment '템플릿 이름',
    send_type                   varchar(30)     not null    comment '발송 타입',
    template_content            text            not null    comment '템플릿 내용',
    is_used                     char default 'N' not null   comment '사용 여부',
    reg_date_time               datetime(6)     not null    comment '등록일자',
    mod_date_time               datetime(6)     not null    comment '수정일자'
)
comment '문자메시지 템플릿 관리 테이블';
