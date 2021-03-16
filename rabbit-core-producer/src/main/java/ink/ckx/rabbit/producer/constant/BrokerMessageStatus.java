package ink.ckx.rabbit.producer.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @auth chenkaixin
 * @date 2021/03/16
 */
@AllArgsConstructor
@Getter
public enum BrokerMessageStatus {

    SENDING("0"),
    SEND_OK("1"),
    SEND_FAIL("2"),
    SEND_FAIL_A_MOMENT("3");

    private String code;
}