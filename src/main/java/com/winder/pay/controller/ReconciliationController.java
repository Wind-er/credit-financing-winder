package com.winder.pay.controller;

import com.winder.common.result.Result;
import com.winder.pay.domain.entity.ReconciliationRecord;
import com.winder.pay.domain.enums.PaymentChannelEnum;
import com.winder.pay.domain.enums.PaymentTypeEnum;
import com.winder.pay.dto.ReconciliationRequest;
import com.winder.pay.service.ReconciliationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 对账 API：提交对账记录、按日期/渠道查询
 */
@RestController
@RequestMapping("/pay/reconciliation")
@RequiredArgsConstructor
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    @PostMapping
    public Result<ReconciliationRecord> save(@RequestBody @Valid ReconciliationRequest request) {
        return Result.ok(reconciliationService.saveReconciliation(request));
    }

    @GetMapping
    public Result<List<ReconciliationRecord>> list(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reconDate,
            @RequestParam(required = false) String channelCode,
            @RequestParam(required = false) String paymentType) {
        List<ReconciliationRecord> list;
        if (channelCode != null && !channelCode.isEmpty() && paymentType != null && !paymentType.isEmpty()) {
            list = reconciliationService.listByDateAndChannel(
                    reconDate, PaymentChannelEnum.valueOf(channelCode), PaymentTypeEnum.valueOf(paymentType));
        } else {
            list = reconciliationService.listByDate(reconDate);
        }
        return Result.ok(list);
    }
}
