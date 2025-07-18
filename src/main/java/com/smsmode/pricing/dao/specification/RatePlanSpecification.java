package com.smsmode.pricing.dao.specification;

import com.smsmode.pricing.model.RatePlanModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

/**
 * Specification class for RatePlanModel queries.
 */
public class RatePlanSpecification {

    /**
     * Creates specification to find rate plans by segment UUID.
     */
    public static Specification<RatePlanModel> withSegmentUuid(String segmentUuid) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(segmentUuid) ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("segment").get("uuid"), segmentUuid);
    }

    /**
     * Creates specification to find rate plans by segment and sub-segment UUIDs.
     */
    public static Specification<RatePlanModel> withSegmentAndSubSegment(String segmentUuid, String subSegmentUuid) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(segmentUuid) || ObjectUtils.isEmpty(subSegmentUuid)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("segment").get("uuid"), segmentUuid),
                    criteriaBuilder.equal(root.get("subSegment").get("uuid"), subSegmentUuid)
            );
        };
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