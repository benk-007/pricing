package com.smsmode.pricing.dao.specification;

import com.smsmode.pricing.model.FeeModel;
import com.smsmode.pricing.model.FeeModel_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import jakarta.persistence.criteria.Join;

import java.util.Set;

public class FeeSpecification {

    public static Specification<FeeModel> withUnitId(String unitId) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(unitId)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(FeeModel_.unit).get("id"), unitId);
        };
    }

    public static Specification<FeeModel> withUnitIds(Set<String> unitIds) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(unitIds)) {
                return criteriaBuilder.conjunction();
            }
            return root.get(FeeModel_.unit).get("id").in(unitIds);
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
