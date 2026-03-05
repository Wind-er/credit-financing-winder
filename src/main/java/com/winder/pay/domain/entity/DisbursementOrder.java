package com.winder.pay.domain.entity;

import com.winder.common.domain.BaseEntity;
import com.winder.pay.domain.enums.OrderStatusEnum;
import com.winder.pay.domain.enums.PaymentChannelEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 放款订单
 */
@Getter
@Setter
public class DisbursementOrder extends BaseEntity {

    private String bizOrderNo;
    private PaymentChannelEnum channelCode;
    private String channelSerialNo;
    private BigDecimal amount;
    private String payeeId;
    private String payeeName;
    private String payeeAccount;
    private String payeeBankCode;
    private OrderStatusEnum status = OrderStatusEnum.PENDING;
    private String failReason;
    private Integer retryCount = 0;
}
