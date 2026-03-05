package com.winder.pay.domain.enums;

import lombok.Getter;

/**
 * 支付/放款/还款订单状态
 */
@Getter
public enum OrderStatusEnum {

    PENDING("PENDING", "待处理"),
    PROCESSING("PROCESSING", "处理中"),
    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败"),
    CLOSED("CLOSED", "已关闭");

    private final String code;
    private final String desc;

    OrderStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
