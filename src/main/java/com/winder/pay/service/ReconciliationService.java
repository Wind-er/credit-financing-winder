package com.winder.pay.service;

import com.winder.pay.domain.entity.ReconciliationRecord;
import com.winder.pay.domain.enums.PaymentChannelEnum;
import com.winder.pay.domain.enums.PaymentTypeEnum;
import com.winder.pay.dto.ReconciliationRequest;
import com.winder.pay.mapper.ReconciliationRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * 对账服务：日终与渠道/资方对账，记录差错
 */
@Service
@RequiredArgsConstructor
public class ReconciliationService {

    private final ReconciliationRecordMapper reconciliationRecordMapper;

    @Transactional(rollbackFor = Exception.class)
    public ReconciliationRecord saveReconciliation(ReconciliationRequest req) {
        Instant now = Instant.now();
        ReconciliationRecord record = new ReconciliationRecord();
        record.setReconDate(req.getReconDate());
        record.setChannelCode(PaymentChannelEnum.valueOf(req.getChannelCode()));
        record.setPaymentType(PaymentTypeEnum.valueOf(req.getPaymentType()));
        record.setChannelSerialNo(req.getChannelSerialNo());
        record.setAmount(req.getAmount());
        record.setBizOrderNo(req.getBizOrderNo());
        record.setReconResult(req.getReconResult());
        record.setRemark(req.getRemark());
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        reconciliationRecordMapper.insert(record);
        return record;
    }

    public List<ReconciliationRecord> listByDateAndChannel(LocalDate reconDate,
                                                            PaymentChannelEnum channelCode,
                                                            PaymentTypeEnum paymentType) {
        return reconciliationRecordMapper.selectByDateAndChannelAndType(reconDate, channelCode, paymentType);
    }

    public List<ReconciliationRecord> listByDate(LocalDate reconDate) {
        return reconciliationRecordMapper.selectByDateOrderByChannelAndType(reconDate);
    }
}
