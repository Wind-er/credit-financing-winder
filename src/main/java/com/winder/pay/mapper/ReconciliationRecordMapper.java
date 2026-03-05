package com.winder.pay.mapper;

import com.winder.pay.domain.entity.ReconciliationRecord;
import com.winder.pay.domain.enums.PaymentChannelEnum;
import com.winder.pay.domain.enums.PaymentTypeEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 对账记录 Mapper
 */
@Mapper
public interface ReconciliationRecordMapper {

    int insert(ReconciliationRecord record);

    List<ReconciliationRecord> selectByDateAndChannelAndType(
            @Param("reconDate") LocalDate reconDate,
            @Param("channelCode") PaymentChannelEnum channelCode,
            @Param("paymentType") PaymentTypeEnum paymentType);

    List<ReconciliationRecord> selectByDateOrderByChannelAndType(@Param("reconDate") LocalDate reconDate);
}
