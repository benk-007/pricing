package com.smsmode.pricing.dao.specification;

import com.smsmode.pricing.model.DefaultRateModel;
import com.smsmode.pricing.model.DefaultRateModel_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

/**
 * Specification class for DefaultRateModel queries
 */
public class DefaultRateSpecification {

    /**
     * Creates a specification to find default rates by unit UUID
     */
    public static Specification<DefaultRateModel> withUnitId(String unitId) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(unitId) ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("unit").get("uuid"), unitId);
    }

    /**
     * Creates a specification to find default rate by ID
     */
    public static Specification<DefaultRateModel> withIdEqual(String rateId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(DefaultRateModel_.id), rateId);
    }
}