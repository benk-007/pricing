package com.smsmode.pricing.dao.specification;

import com.smsmode.pricing.embeddable.SegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.SegmentRefEmbeddable_;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable_;
import com.smsmode.pricing.enumeration.RateTableTypeEnum;
import com.smsmode.pricing.model.RatePlanModel;
import com.smsmode.pricing.model.RatePlanModel_;
import com.smsmode.pricing.model.RateTableModel;
import com.smsmode.pricing.model.RateTableModel_;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
                                criteriaBuilder.lower(root.get(RatePlanModel_.name)),
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

            Join<RatePlanModel, SegmentRefEmbeddable> segmentJoin = root.join(RatePlanModel_.segments);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get(RatePlanModel_.enabled), true),
                    segmentJoin.get(SegmentRefEmbeddable_.id).in(segmentUuids)
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
                                criteriaBuilder.lower(root.join(RatePlanModel_.segments).get(SegmentRefEmbeddable_.name)),
                                "%" + segmentName.toLowerCase() + "%"
                        );
    }


    /**
     * Creates specification to find rate plans by unit UUID.
     */
    public static Specification<RatePlanModel> withUnitUuid(String unitUuid) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(unitUuid) ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get(RatePlanModel_.unit).get(UnitRefEmbeddable_.id), unitUuid);
    }

    public static Specification<RatePlanModel> withEnabled(Boolean b) {
        return (root, query, criteriaBuilder) ->
                b == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get(RatePlanModel_.enabled), b);
    }

    public static Specification<RatePlanModel> withNoSegments() {
        return (root, query, cb) -> cb.isEmpty(root.get(RatePlanModel_.segments));
    }

    public static Specification<RatePlanModel> withSegmentId(String segmentId) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(segmentId)) {
                return criteriaBuilder.conjunction();
            } else {
                Join<RatePlanModel, SegmentRefEmbeddable> segmentsJoin = root.join(RatePlanModel_.segments, JoinType.LEFT);
                return criteriaBuilder.equal(segmentsJoin.get(SegmentRefEmbeddable_.id), segmentId);
            }
        };
    }

    public static Specification<RatePlanModel> withUnitId(String unitId) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(unitId) ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get(RatePlanModel_.unit).get(UnitRefEmbeddable_.id), unitId);
    }

    public static Specification<RateTableModel> withType(RateTableTypeEnum type) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(type) ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get(RateTableModel_.type), type);
    }

    public static Specification<RatePlanModel> withStandard(boolean value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(RatePlanModel_.standard), value);
    }
}