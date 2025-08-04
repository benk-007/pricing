package com.smsmode.pricing.model;

import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.enumeration.FeeModalityEnum;
import com.smsmode.pricing.enumeration.FeeTypeEnum;
import com.smsmode.pricing.model.base.AbstractBaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "FEE")
public class FeeModel extends AbstractBaseModel {

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private FeeTypeEnum type;

    @Enumerated(EnumType.STRING)
    @Column(name = "MODALITY", nullable = false)
    private FeeModalityEnum modality;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active = true;

    @Embedded
    private UnitRefEmbeddable unit;
}
