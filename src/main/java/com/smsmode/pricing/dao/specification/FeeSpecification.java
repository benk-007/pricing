package com.smsmode.pricing.dao.specification;

import com.smsmode.pricing.model.FeeModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import jakarta.persistence.criteria.Join;

public class FeeSpecification {

    public static Specification<FeeModel> withUnitId(String unitId) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(unitId)) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> unitsJoin = root.join("units");
            return criteriaBuilder.equal(unitsJoin.get("id"), unitId);
        };
    }

    public static Specification<FeeModel> withNameContaining(String name) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(name)) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }
}
