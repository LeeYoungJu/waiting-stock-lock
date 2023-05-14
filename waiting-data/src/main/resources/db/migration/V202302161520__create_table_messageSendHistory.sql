create table cw_message_send_history
(
    seq  bigint auto_increment comment '메시지 발송 시퀀스'
        primary key,
    waiting_history_seq         bigint          not null    comment '웨이팅 히스토리 시퀀스',
    request_id                  varchar(30)     not null    comment '웨이팅 히스토리 시퀀스',
    enc_customer_phone          varchar(80)     not null    comment '고객 연락처',
    send_channel                varchar(10)     not null    comment '발송 채널 - ALIMTALK - 알림톡, SMS - 문자',
    template_name               varchar(50)     not null    comment '템플릿 이름',
    send_type                   varchar(20)     not null    comment '발송 타입',
    template_code               varchar(50)     not null    comment '템플릿 코드 - 알림톡에만 있음',
    content                     text            not null    comment '발송 내용',
    buttons                     text            not null    comment '발송 버튼',
    status                      varchar(50)     not null    comment '발송 상태 ',
    fail_code                   varchar(20)     not null    comment '발송 실패 코드',
    fail_reason                 text            not null    comment '발송 실패 사유',
    send_date_time              datetime(6)     not null    comment '발송일자',
    reg_date_time               datetime(6)     not null    comment '등록일자',
    mod_date_time               datetime(6)     not null    comment '수정일자'
)
comment '메시지 발송 히스토리 테이블';

create index cw_message_send_history_waiting_history_seq_index
    on cw_message_send_history (waiting_history_seq);