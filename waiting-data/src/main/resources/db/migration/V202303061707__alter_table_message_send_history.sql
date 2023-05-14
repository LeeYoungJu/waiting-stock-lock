alter table cw_message_send_history
    modify buttons text null comment '발송 버튼';


alter table cw_message_send_history
    modify fail_code varchar(20) null comment '발송 실패 코드';

alter table cw_message_send_history
    modify fail_reason text null comment '발송 실패 사유';

alter table cw_message_send_history
    modify fail_reason text null comment '발송 실패 사유';

alter table cw_message_send_history
    modify send_type varchar(50) not null comment '발송 타입';
