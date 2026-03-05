package com.winder.pay.service;

import com.winder.common.exception.BusinessException;
import com.winder.pay.adapter.PaymentChannelAdapter;
import com.winder.pay.config.PayProperties;
import com.winder.pay.domain.entity.RepaymentOrder;
import com.winder.pay.domain.enums.OrderStatusEnum;
import com.winder.pay.domain.enums.PaymentChannelEnum;
import com.winder.pay.dto.RepaymentRequest;
import com.winder.pay.mapper.RepaymentOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * 还款服务：主动还款/代扣 — 创建还款单、调用渠道、更新状态
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepaymentService {

    private final RepaymentOrderMapper repaymentOrderMapper;
    private final PaymentChannelService channelService;
    private final PayProperties payProperties;

    @Transactional(rollbackFor = Exception.class)
    public RepaymentOrder createAndExecute(RepaymentRequest req) {
        if (repaymentOrderMapper.selectByBizRepayNo(req.getBizRepayNo()) != null) {
            throw new BusinessException(409, "还款单号已存在: " + req.getBizRepayNo());
        }
        PaymentChannelEnum channel = req.getChannelCode() != null && !req.getChannelCode().isEmpty()
                ? PaymentChannelEnum.valueOf(req.getChannelCode())
                : payProperties.getDefaultChannel();
        PaymentChannelAdapter adapter = channelService.getAdapter(channel);

        Instant now = Instant.now();
        RepaymentOrder order = new RepaymentOrder();
        order.setBizRepayNo(req.getBizRepayNo());
        order.setLoanNo(req.getLoanNo());
        order.setChannelCode(channel);
        order.setAmount(req.getAmount());
        order.setPayerId(req.getPayerId());
        order.setPayerAccount(req.getPayerAccount());
        order.setStatus(OrderStatusEnum.PROCESSING);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        repaymentOrderMapper.insert(order);

        int maxRetry = payProperties.getRetry().getMaxAttempts();
        for (int i = 0; i < maxRetry; i++) {
            try {
                PaymentChannelAdapter.RepaymentResult result = adapter.repay(
                        order.getBizRepayNo(), order.getLoanNo(), order.getAmount(),
                        order.getPayerId(), order.getPayerAccount());
                if (result.success()) {
                    order.setChannelSerialNo(result.channelSerialNo());
                    order.setStatus(OrderStatusEnum.SUCCESS);
                    order.setRetryCount(i + 1);
                    order.setUpdatedAt(Instant.now());
                    repaymentOrderMapper.updateById(order);
                    return order;
                }
                order.setFailReason(result.failReason());
            } catch (Exception e) {
                log.warn("Repay attempt {} failed: {}", i + 1, e.getMessage());
                order.setFailReason(e.getMessage());
                order.setRetryCount(i + 1);
            }
        }
        order.setStatus(OrderStatusEnum.FAILED);
        order.setUpdatedAt(Instant.now());
        repaymentOrderMapper.updateById(order);
        return order;
    }

    public RepaymentOrder getByBizRepayNo(String bizRepayNo) {
        RepaymentOrder order = repaymentOrderMapper.selectByBizRepayNo(bizRepayNo);
        if (order == null) {
            throw new BusinessException(404, "还款单不存在: " + bizRepayNo);
        }
        return order;
    }

    public List<RepaymentOrder> listByLoanNo(String loanNo) {
        return repaymentOrderMapper.selectByLoanNoOrderByCreatedAtDesc(loanNo);
    }
}
