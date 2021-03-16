CREATE TABLE IF NOT EXISTS `broker_message`
(
    `message_id`      varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '消息id',
    `message`         varchar(4000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息体',
    `try_count`       int(0)                                                   NULL DEFAULT 0 COMMENT '重试次数',
    `status`          varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT '' COMMENT '状态',
    `next_retry_time` timestamp(0)                                             NOT NULL COMMENT '下一次重试时间',
    `create_time`     timestamp(0)                                             NOT NULL COMMENT '创建时间',
    `update_time`     timestamp(0)                                             NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = DYNAMIC;
