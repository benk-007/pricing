/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.model.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Abstract base class for entities that require common auditing fields.
 * <p>This class provides fields for tracking entity creation and modification details,
 * including creation date, creator, modification date, and modifier.</p>
 * <p>It is intended to be inherited by other entities that need these common fields.</p>
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 18 Oct 2024
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractBaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    protected String id;

    @CreatedDate
    @Column(name = "CREATED_AT")
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "MODIFIED_AT")
    protected LocalDateTime modifiedAt;

    @CreatedBy
    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "MODIFIED_BY", nullable = false)
    private String modifiedBy;
}
