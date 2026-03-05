package com.winder.pay.mapper;

import com.winder.pay.domain.entity.PaymentChannelConfig;
import com.winder.pay.domain.enums.PaymentChannelEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付渠道配置 Mapper
 */
@Mapper
public interface PaymentChannelConfigMapper {

    int insert(PaymentChannelConfig config);

    PaymentChannelConfig selectByChannelCodeAndEnabled(@Param("channelCode") PaymentChannelEnum channelCode);

    List<PaymentChannelConfig> selectByEnabledTrue();
}
