package com.smsmode.pricing.service.impl;

import com.smsmode.pricing.dao.service.RatePlanDaoService;
import com.smsmode.pricing.embeddable.SegmentRefEmbeddable;
import com.smsmode.pricing.mapper.RatePlanMapper;
import com.smsmode.pricing.model.RatePlanModel;
import com.smsmode.pricing.resource.rateplan.RatePlanGetResource;
import com.smsmode.pricing.resource.rateplan.RatePlanPatchResource;
import com.smsmode.pricing.resource.rateplan.RatePlanPostResource;
import com.smsmode.pricing.service.RatePlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of RatePlanService for managing rate plan business operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RatePlanServiceImpl implements RatePlanService {

    private final RatePlanMapper ratePlanMapper;
    private final RatePlanDaoService ratePlanDaoService;

    @Override
    public ResponseEntity<RatePlanGetResource> create(RatePlanPostResource ratePlanPostResource) {
        log.debug("Creating rate plan: {}", ratePlanPostResource.getName());

        // Transform POST resource to model
        RatePlanModel ratePlanModel = ratePlanMapper.postResourceToModel(ratePlanPostResource);

        // If new rate plan is enabled, disable competitors
        if (Boolean.TRUE.equals(ratePlanModel.getEnabled())) {
            disableCompetitors(ratePlanModel);
        }

        // Save to database
        ratePlanModel = ratePlanDaoService.save(ratePlanModel);
        log.info("Successfully created rate plan with ID: {}", ratePlanModel.getId());

        // Transform model to GET resource
        RatePlanGetResource ratePlanGetResource = ratePlanMapper.modelToGetResource(ratePlanModel);

        return ResponseEntity.created(URI.create("")).body(ratePlanGetResource);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<RatePlanGetResource>> getAll(String unitId, String search, String segmentName, Pageable pageable) {
        log.debug("Retrieving all rate plans with pagination");

        // Get paginated data from database
        Page<RatePlanModel> ratePlanModelPage = ratePlanDaoService.findByUnitId(unitId, search, segmentName, pageable);
        log.info("Retrieved {} rate plans from database", ratePlanModelPage.getTotalElements());

        // Transform models to GET resources
        Page<RatePlanGetResource> response = ratePlanModelPage.map(ratePlanMapper::modelToGetResource);

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<RatePlanGetResource> getById(String ratePlanId) {
        log.debug("Retrieving rate plan by ID: {}", ratePlanId);

        // Find by ID (throws exception if not found)
        RatePlanModel ratePlanModel = ratePlanDaoService.findById(ratePlanId);

        // Transform model to GET resource
        RatePlanGetResource response = ratePlanMapper.modelToGetResource(ratePlanModel);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RatePlanGetResource> update(String ratePlanId, RatePlanPatchResource ratePlanPatchResource) {
        log.debug("Updating rate plan with ID: {}", ratePlanId);

        // Find existing rate plan
        RatePlanModel existingRatePlan = ratePlanDaoService.findById(ratePlanId);
        log.debug("Found existing rate plan: {}", existingRatePlan.getId());

        // Update model with new data
        ratePlanMapper.updateModelFromPatchResource(ratePlanPatchResource, existingRatePlan);

        // If updated rate plan is enabled, disable competitors
        if (Boolean.TRUE.equals(existingRatePlan.getEnabled())) {
            disableCompetitors(existingRatePlan);
        }

        // Save updated model
        RatePlanModel updatedRatePlan = ratePlanDaoService.save(existingRatePlan);
        log.info("Successfully updated rate plan with ID: {}", ratePlanId);

        // Transform model to GET resource
        RatePlanGetResource response = ratePlanMapper.modelToGetResource(updatedRatePlan);

        return ResponseEntity.ok(response);
    }

    /**
     * Disables competitor rate plans with same combination.
     */
    private void disableCompetitors(RatePlanModel ratePlanModel) {
        log.debug("Disabling competitors for rate plan: {}", ratePlanModel.getName());

        // Extraire les UUIDs des segments
        Set<String> segmentUuids = ratePlanModel.getSegment().stream()
                .map(SegmentRefEmbeddable::getUuid)
                .collect(Collectors.toSet());

        // Trouver les concurrents avec la même combinaison de segments
        List<RatePlanModel> competitors = ratePlanDaoService.findEnabledRatePlansWithSameCombination(segmentUuids);

        // Supprimer le rate plan actuel de la liste des concurrents (pour le cas UPDATE)
        competitors.removeIf(competitor -> competitor.getId().equals(ratePlanModel.getId()));

        if (!competitors.isEmpty()) {
            log.info("Found {} competitors to disable", competitors.size());
            ratePlanDaoService.disableRatePlans(competitors);
        } else {
            log.debug("No competitors found");
        }
    }

    @Override
    public ResponseEntity<Void> delete(String ratePlanId) {
        log.debug("Deleting rate plan with ID: {}", ratePlanId);

        // Find existing rate plan (throws exception if not found)
        RatePlanModel existingRatePlan = ratePlanDaoService.findById(ratePlanId);

        // Delete from database
        ratePlanDaoService.delete(existingRatePlan);
        log.info("Successfully deleted rate plan with ID: {}", ratePlanId);

        return ResponseEntity.noContent().build();
    }
}