package com.smsmode.pricing.dao.service.impl;

import com.smsmode.pricing.dao.repository.RatePlanRepository;
import com.smsmode.pricing.dao.service.RatePlanDaoService;
import com.smsmode.pricing.dao.specification.RatePlanSpecification;
import com.smsmode.pricing.exception.ResourceNotFoundException;
import com.smsmode.pricing.exception.enumeration.ResourceNotFoundExceptionTitleEnum;
import com.smsmode.pricing.model.RatePlanModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Implementation of RatePlanDaoService for managing rate plan data access.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RatePlanDaoServiceImpl implements RatePlanDaoService {

    private final RatePlanRepository ratePlanRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public RatePlanModel save(RatePlanModel ratePlanModel) {
        log.debug("Saving rate plan: {}", ratePlanModel.getName());
        return ratePlanRepository.save(ratePlanModel);
    }

    @Override
    public List<RatePlanModel> findEnabledRatePlansWithSameCombination(Set<String> segmentUuids) {
        log.debug("Finding enabled rate plans with segments: {}", segmentUuids);

        Specification<RatePlanModel> spec;

        if (CollectionUtils.isEmpty(segmentUuids)) {
            // Cas où aucun segment n'est fourni - chercher les rate plans sans segments
            spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.isEmpty(root.get("segment")),
                    criteriaBuilder.equal(root.get("enabled"), true)
            );
        }else {
            // Utiliser une requête JPQL pour comparer les Sets de segments
            String jpql = """
        SELECT DISTINCT rp FROM RatePlanModel rp 
        LEFT JOIN rp.segment s
        WHERE rp.enabled = true 
        GROUP BY rp.id, rp.name, rp.enabled, rp.unit, rp.createdAt, rp.modifiedAt, rp.createdBy, rp.modifiedBy
        HAVING COUNT(s) = :segmentCount
        AND COUNT(CASE WHEN s.uuid IN :segmentUuids THEN 1 END) = :segmentCount
    """;

            TypedQuery<RatePlanModel> query = entityManager.createQuery(jpql, RatePlanModel.class);
            query.setParameter("segmentUuids", segmentUuids);
            query.setParameter("segmentCount", (long) segmentUuids.size());

            return query.getResultList();
        }

        return ratePlanRepository.findAll(spec);
    }

    @Override
    public void disableRatePlans(List<RatePlanModel> ratePlansToDisable) {
        log.debug("Disabling {} rate plans", ratePlansToDisable.size());
        for (RatePlanModel ratePlan : ratePlansToDisable) {
            ratePlan.setEnabled(false);
            ratePlanRepository.save(ratePlan);
        }
    }

    @Override
    public Page<RatePlanModel> findByUnitId(String unitId, String search, String segmentName, Pageable pageable) {
        log.debug("Finding rate plans for unit: {} with filters - search: {}, segmentName: {}",
                unitId, search, segmentName);

        // Build specification with all filters
        Specification<RatePlanModel> specification = Specification
                .where(RatePlanSpecification.withUnitUuid(unitId))
                .and(RatePlanSpecification.withNameContaining(search))
                .and(RatePlanSpecification.withSegmentNamesContaining(segmentName));
        return ratePlanRepository.findAll(specification, pageable);
    }

    @Override
    public RatePlanModel findById(String ratePlanId) {
        log.debug("Finding rate plan by ID: {}", ratePlanId);
        return ratePlanRepository.findById(ratePlanId).orElseThrow(() -> {
            log.debug("Rate plan with ID [{}] not found", ratePlanId);
            return new ResourceNotFoundException(
                    ResourceNotFoundExceptionTitleEnum.RATE_PLAN_NOT_FOUND,
                    "Rate plan with ID [" + ratePlanId + "] not found");
        });
    }

    @Override
    public void delete(RatePlanModel ratePlanModel) {
        log.debug("Deleting rate plan: {}", ratePlanModel.getId());
        ratePlanRepository.delete(ratePlanModel);
    }
}