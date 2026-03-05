package com.winder.pay.domain.entity;

import com.winder.common.domain.BaseEntity;
import com.winder.pay.domain.enums.PaymentChannelEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 支付渠道配置
 */
@Getter
@Setter
public class PaymentChannelConfig extends BaseEntity {

    private PaymentChannelEnum channelCode;
    private String channelName;
    private Boolean enabled = true;
    private Boolean disbursementEnabled = true;
    private Boolean repaymentEnabled = true;
    private Long singleLimitCents;
    private String extraConfig;
}
