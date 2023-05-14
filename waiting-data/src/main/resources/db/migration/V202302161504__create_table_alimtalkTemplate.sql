create table cw_message_alimtalk_template
(
    seq  bigint auto_increment comment '알림톡 템플릿 시퀀스'
        primary key,
    template_name               varchar(50)     not null    comment '템플릿 이름',
    send_type                   varchar(30)     not null    comment '발송 타입',
    template_code               varchar(50)     not null    comment '템플릿 코드',
    template_content            text            not null    comment '템플릿 내용',
    template_buttons            text            null        comment '템플릿 버튼',
    template_status             varchar(10)     not null    comment '템플릿 상태 - 요청, 검수중, 승인, 반려',
    is_used                     char default 'N' not null   comment '사용 여부',
    reg_date_time               datetime(6)     not null    comment '등록일자',
    mod_date_time               datetime(6)     not null    comment '수정일자'
)
comment '알림톡 템플릿 관리 테이블';
