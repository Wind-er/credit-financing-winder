package com.winder.pay.domain.enums;

import lombok.Getter;

/**
 * 支付类型：放款 / 还款
 */
@Getter
public enum PaymentTypeEnum {

    DISBURSEMENT("DISBURSEMENT", "放款"),
    REPAYMENT("REPAYMENT", "还款");

    private final String code;
    private final String desc;

    PaymentTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
