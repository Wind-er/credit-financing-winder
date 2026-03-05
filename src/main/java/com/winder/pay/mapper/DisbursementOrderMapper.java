package com.winder.pay.mapper;

import com.winder.pay.domain.entity.DisbursementOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 放款订单 Mapper
 */
@Mapper
public interface DisbursementOrderMapper {

    int insert(DisbursementOrder order);

    int updateById(DisbursementOrder order);

    DisbursementOrder selectByBizOrderNo(@Param("bizOrderNo") String bizOrderNo);

    boolean existsByBizOrderNo(@Param("bizOrderNo") String bizOrderNo);
}
