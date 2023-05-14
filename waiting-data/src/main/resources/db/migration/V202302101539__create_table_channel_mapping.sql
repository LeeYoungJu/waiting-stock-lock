CREATE TABLE cw_channel_mapping
(
    seq             BIGINT AUTO_INCREMENT COMMENT '매핑 시퀀스' PRIMARY KEY,
    channel_id      VARCHAR(28)         NOT NULL COMMENT '채널 아이디',
    channel_shop_id VARCHAR(64)         NOT NULL COMMENT '채널의 매장 아이디',
    shop_id         VARCHAR(64)         NOT NULL COMMENT '웨이팅 매장 아이디',
    connected_yn    CHAR(1) DEFAULT 'N' NOT NULL COMMENT '연결(사용중) 여부.',
    reg_date_time   DATETIME(6) NOT NULL COMMENT '등록일자',
    mod_date_time   DATETIME(6) NOT NULL COMMENT '수정일자'
) comment '채널 매장 매핑 테이블';

CREATE INDEX cw_channel_mapping_channel_shop_composite_index
    ON cw_channel_mapping (channel_id, channel_shop_id, shop_id);

CREATE INDEX cw_channel_mapping_shop_index
    ON cw_channel_mapping (channel_id, shop_id);
