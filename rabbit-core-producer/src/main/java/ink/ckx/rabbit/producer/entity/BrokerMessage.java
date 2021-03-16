package ink.ckx.rabbit.producer.entity;

import lombok.Data;

import java.util.Date;

/**
 * @auth chenkaixin
 * @date 2021/03/16
 */
@Data
public class BrokerMessage {

    private String messageId;

    private String message;

    private Integer tryCount = 0;

    private String status;

    private Date nextRetryTime;

    private Date createTime;

    private Date updateTime;
}