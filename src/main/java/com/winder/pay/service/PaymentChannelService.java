package com.winder.pay.service;

import com.winder.pay.adapter.PaymentChannelAdapter;
import com.winder.pay.config.PayProperties;
import com.winder.pay.domain.enums.PaymentChannelEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 根据配置选择支付渠道适配器
 */
@Service
@RequiredArgsConstructor
public class PaymentChannelService {

    private final List<PaymentChannelAdapter> adapters;
    private final PayProperties payProperties;

    private Map<PaymentChannelEnum, PaymentChannelAdapter> adapterMap;

    private Map<PaymentChannelEnum, PaymentChannelAdapter> getAdapterMap() {
        if (adapterMap == null) {
            adapterMap = adapters.stream()
                    .collect(Collectors.toMap(PaymentChannelAdapter::getChannelCode, Function.identity()));
        }
        return adapterMap;
    }

    public PaymentChannelAdapter getDefaultAdapter() {
        return getAdapter(payProperties.getDefaultChannel());
    }

    public PaymentChannelAdapter getAdapter(PaymentChannelEnum channel) {
        return Optional.ofNullable(getAdapterMap().get(channel))
                .orElseThrow(() -> new IllegalStateException("Unknown or unsupported channel: " + channel));
    }
}
