package com.smsmode.pricing.model;

import com.smsmode.pricing.embeddable.PropertyRefEmbeddable;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.enumeration.FeeModalityEnum;
import com.smsmode.pricing.model.base.AbstractBaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "MODALITY", nullable = false)
    private FeeModalityEnum modality;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active = true;

    @Column(name = "REQUIRED", nullable = false)
    private boolean required = false;

    @Embedded
    private UnitRefEmbeddable unit;

    @Embedded
    private PropertyRefEmbeddable property;

    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<AdditionalGuestFeeModel> additionalGuestPrices = new ArrayList<>();
}
