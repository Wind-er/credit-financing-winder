package com.winder.pay.domain.enums;

import lombok.Getter;

/**
 * 支付渠道枚举
 */
@Getter
public enum PaymentChannelEnum {

    MOCK("MOCK", "模拟渠道"),
    BANK("BANK", "银行直连"),
    THIRD_PARTY("THIRD_PARTY", "第三方支付");

    private final String code;
    private final String desc;

    PaymentChannelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
