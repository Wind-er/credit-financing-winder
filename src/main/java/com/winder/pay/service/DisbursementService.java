package com.winder.pay.service;

import com.winder.common.exception.BusinessException;
import com.winder.pay.adapter.PaymentChannelAdapter;
import com.winder.pay.config.PayProperties;
import com.winder.pay.domain.entity.DisbursementOrder;
import com.winder.pay.domain.enums.OrderStatusEnum;
import com.winder.pay.domain.enums.PaymentChannelEnum;
import com.winder.pay.dto.DisbursementRequest;
import com.winder.pay.mapper.DisbursementOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * 放款服务：创建放款单、调用渠道、更新状态
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DisbursementService {

    private final DisbursementOrderMapper disbursementOrderMapper;
    private final PaymentChannelService channelService;
    private final PayProperties payProperties;

    @Transactional(rollbackFor = Exception.class)
    public DisbursementOrder createAndExecute(DisbursementRequest req) {
        if (disbursementOrderMapper.existsByBizOrderNo(req.getBizOrderNo())) {
            throw new BusinessException(409, "放款单号已存在: " + req.getBizOrderNo());
        }
        PaymentChannelEnum channel = req.getChannelCode() != null && !req.getChannelCode().isEmpty()
                ? PaymentChannelEnum.valueOf(req.getChannelCode())
                : payProperties.getDefaultChannel();
        PaymentChannelAdapter adapter = channelService.getAdapter(channel);

        Instant now = Instant.now();
        DisbursementOrder order = new DisbursementOrder();
        order.setBizOrderNo(req.getBizOrderNo());
        order.setChannelCode(channel);
        order.setAmount(req.getAmount());
        order.setPayeeId(req.getPayeeId());
        order.setPayeeName(req.getPayeeName());
        order.setPayeeAccount(req.getPayeeAccount());
        order.setPayeeBankCode(req.getPayeeBankCode());
        order.setStatus(OrderStatusEnum.PROCESSING);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        disbursementOrderMapper.insert(order);

        int maxRetry = payProperties.getRetry().getMaxAttempts();
        for (int i = 0; i < maxRetry; i++) {
            try {
                PaymentChannelAdapter.DisbursementResult result = adapter.disburse(
                        order.getBizOrderNo(), order.getAmount(),
                        order.getPayeeId(), order.getPayeeName(), order.getPayeeAccount(), order.getPayeeBankCode());
                if (result.success()) {
                    order.setChannelSerialNo(result.channelSerialNo());
                    order.setStatus(OrderStatusEnum.SUCCESS);
                    order.setRetryCount(i + 1);
                    order.setUpdatedAt(Instant.now());
                    disbursementOrderMapper.updateById(order);
                    return order;
                }
                order.setFailReason(result.failReason());
            } catch (Exception e) {
                log.warn("Disburse attempt {} failed: {}", i + 1, e.getMessage());
                order.setFailReason(e.getMessage());
                order.setRetryCount(i + 1);
            }
        }
        order.setStatus(OrderStatusEnum.FAILED);
        order.setUpdatedAt(Instant.now());
        disbursementOrderMapper.updateById(order);
        return order;
    }

    public DisbursementOrder getByBizOrderNo(String bizOrderNo) {
        DisbursementOrder order = disbursementOrderMapper.selectByBizOrderNo(bizOrderNo);
        if (order == null) {
            throw new BusinessException(404, "放款单不存在: " + bizOrderNo);
        }
        return order;
    }
}
