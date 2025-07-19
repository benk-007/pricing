package com.smsmode.pricing.dao.specification;

import com.smsmode.pricing.model.RatePlanModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

/**
 * Specification class for RatePlanModel queries.
 */
public class RatePlanSpecification {

    /**
     * Creates specification to search by rate plan name.
     */
    public static Specification<RatePlanModel> withNameContaining(String name) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(name) ? criteriaBuilder.conjunction() :
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + name.toLowerCase() + "%"
                        );
    }

    /**
     * Creates specification to search by segment name.
     */
    public static Specification<RatePlanModel> withSegmentNameContaining(String segmentName) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(segmentName) ? criteriaBuilder.conjunction() :
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("segment").get("name")),
                                "%" + segmentName.toLowerCase() + "%"
                        );
    }

    /**
     * Creates specification to search by sub-segment name.
     */
    public static Specification<RatePlanModel> withSubSegmentNameContaining(String subSegmentName) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(subSegmentName) ? criteriaBuilder.conjunction() :
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("subSegment").get("name")),
                                "%" + subSegmentName.toLowerCase() + "%"
                        );
    }

    /**
     * Creates specification to find rate plans by unit UUID.
     */
    public static Specification<RatePlanModel> withUnitUuid(String unitUuid) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(unitUuid) ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("unit").get("uuid"), unitUuid);
    }
}