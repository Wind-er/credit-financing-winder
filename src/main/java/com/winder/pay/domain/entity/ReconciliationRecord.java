package com.winder.pay.domain.entity;

import com.winder.common.domain.BaseEntity;
import com.winder.pay.domain.enums.PaymentChannelEnum;
import com.winder.pay.domain.enums.PaymentTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 对账记录（与渠道/资方日终对账）
 */
@Getter
@Setter
public class ReconciliationRecord extends BaseEntity {

    private LocalDate reconDate;
    private PaymentChannelEnum channelCode;
    private PaymentTypeEnum paymentType;
    private String channelSerialNo;
    private BigDecimal amount;
    private String bizOrderNo;
    private String reconResult;
    private String remark;
}
