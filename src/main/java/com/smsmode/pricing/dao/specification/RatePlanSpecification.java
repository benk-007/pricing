package com.smsmode.pricing.dao.specification;

import com.smsmode.pricing.embeddable.SegmentRefEmbeddable;
import com.smsmode.pricing.model.RatePlanModel;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Set;

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
     * Finds rate plans that have at least one segment in common with the given segments.
     */
    public static Specification<RatePlanModel> withOverlappingSegments(Set<String> segmentUuids) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(segmentUuids)) {
                return criteriaBuilder.disjunction(); // Retourne false (aucun résultat)
            }

            Join<RatePlanModel, SegmentRefEmbeddable> segmentJoin = root.join("segment");
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("enabled"), true),
                    segmentJoin.get("uuid").in(segmentUuids)
            );
        };
    }

    /**
     * Creates specification to search by segment names in the Set.
     */
    public static Specification<RatePlanModel> withSegmentNamesContaining(String segmentName) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(segmentName) ? criteriaBuilder.conjunction() :
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.join("segment").get("name")),
                                "%" + segmentName.toLowerCase() + "%"
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