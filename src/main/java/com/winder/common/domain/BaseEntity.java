package com.winder.common.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * 实体基类：主键、创建/更新时间（MyBatis 使用，在 Service 中设置时间）
 */
@Getter
@Setter
public abstract class BaseEntity {

    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
}
