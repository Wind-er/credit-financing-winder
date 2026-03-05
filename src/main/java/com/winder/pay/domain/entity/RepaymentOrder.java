package com.winder.pay.domain.entity;

import com.winder.common.domain.BaseEntity;
import com.winder.pay.domain.enums.OrderStatusEnum;
import com.winder.pay.domain.enums.PaymentChannelEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 还款订单（主动还款 / 代扣）
 */
@Getter
@Setter
public class RepaymentOrder extends BaseEntity {

    private String bizRepayNo;
    private String loanNo;
    private PaymentChannelEnum channelCode;
    private String channelSerialNo;
    private BigDecimal amount;
    private String payerId;
    private String payerAccount;
    private OrderStatusEnum status = OrderStatusEnum.PENDING;
    private String failReason;
    private Integer retryCount = 0;
}
