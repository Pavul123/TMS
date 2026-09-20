package com.transport.tms.governance.approval.service;

import com.transport.tms.common.exception.Exceptions;
import com.transport.tms.common.util.IdGenerator;
import com.transport.tms.governance.audit.service.AuditService;
import com.transport.tms.governance.correction.dto.CorrectionDto;
import com.transport.tms.governance.correction.entity.CorrectionRequest;
import com.transport.tms.governance.correction.entity.CorrectionRequestItem;
import com.transport.tms.governance.correction.repository.CorrectionRequestRepository;
import com.transport.tms.security.UserPrincipal;
import com.transport.tms.trip.entity.Trip;
import com.transport.tms.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final CorrectionRequestRepository correctionRepository;
    private final TripRepository tripRepository;
    private final AuditService auditService;
    private final IdGenerator idGenerator;

    @Transactional
    public CorrectionRequest submitCorrectionRequest(CorrectionDto.SubmitCorrectionRequest request, UserPrincipal currentUser) {
        String reqId = idGenerator.generateCorrectionId();
        String identifier = request.getEntityType() + " #" + request.getEntityId();

        CorrectionRequest correction = CorrectionRequest.builder()
                .id(reqId)
                .entityType(request.getEntityType().toUpperCase())
                .entityId(request.getEntityId())
                .entityIdentifier(identifier)
                .reason(request.getReason())
                .status("PENDING")
                .requestedBy(currentUser.getFullName())
                .requestedAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        // If entity is TRIP, snapshot old values
        if ("TRIP".equalsIgnoreCase(request.getEntityType())) {
            Trip trip = tripRepository.findById(request.getEntityId())
                    .orElseThrow(() -> new Exceptions.ResourceNotFoundException("Trip", "id", request.getEntityId()));
            
            identifier = "Trip #" + trip.getId() + " (" + trip.getCustomerName() + ")";
            correction.setEntityIdentifier(identifier);

            for (CorrectionDto.FieldChangeRequest change : request.getChanges()) {
                String oldValue = getTripFieldValue(trip, change.getField());
                CorrectionRequestItem item = CorrectionRequestItem.builder()
                        .request(correction)
                        .fieldName(change.getField())
                        .oldValue(oldValue)
                        .requestedValue(change.getNewValue())
                        .build();
                correction.getItems().add(item);
            }
        }

        return correctionRepository.save(correction);
    }

    @Transactional(readOnly = true)
    public List<CorrectionRequest> getPendingApprovals() {
        return correctionRepository.findByStatusOrderByRequestedAtDesc("PENDING");
    }

    @Transactional(readOnly = true)
    public CorrectionRequest getCorrectionById(String id) {
        return correctionRepository.findById(id)
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException("CorrectionRequest", "id", id));
    }

    @Transactional
    public CorrectionRequest approveCorrection(String id, String comment, UserPrincipal currentUser) {
        CorrectionRequest request = getCorrectionById(id);

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new Exceptions.BadRequestException("Correction request is already " + request.getStatus());
        }

        // Apply changes to target entity
        if ("TRIP".equalsIgnoreCase(request.getEntityType())) {
            Trip trip = tripRepository.findById(request.getEntityId())
                    .orElseThrow(() -> new Exceptions.ResourceNotFoundException("Trip", "id", request.getEntityId()));

            for (CorrectionRequestItem item : request.getItems()) {
                applyTripFieldChange(trip, item.getFieldName(), item.getRequestedValue());
                
                // Record audit log entry
                auditService.recordAudit(
                        "TRIP",
                        trip.getId(),
                        "UPDATE",
                        item.getFieldName(),
                        item.getOldValue(),
                        item.getRequestedValue(),
                        request.getReason(),
                        currentUser.getFullName(),
                        request.getId()
                );
            }
            tripRepository.save(trip);
        }

        request.setStatus("APPROVED");
        request.setReviewedBy(currentUser.getFullName());
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewComment(comment);

        return correctionRepository.save(request);
    }

    @Transactional
    public CorrectionRequest rejectCorrection(String id, String comment, UserPrincipal currentUser) {
        CorrectionRequest request = getCorrectionById(id);

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new Exceptions.BadRequestException("Correction request is already " + request.getStatus());
        }

        request.setStatus("REJECTED");
        request.setReviewedBy(currentUser.getFullName());
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewComment(comment);

        auditService.recordAudit(
                request.getEntityType(),
                request.getEntityId(),
                "REJECT_CORRECTION",
                "status",
                "PENDING",
                "REJECTED",
                comment != null ? comment : "Correction rejected by reviewer",
                currentUser.getFullName(),
                request.getId()
        );

        return correctionRepository.save(request);
    }

    private String getTripFieldValue(Trip trip, String fieldName) {
        return switch (fieldName.toLowerCase()) {
            case "quantity" -> trip.getQuantity() != null ? trip.getQuantity().toString() : "";
            case "material" -> trip.getMaterial();
            case "appliedrate", "rate" -> trip.getAppliedRate() != null ? trip.getAppliedRate().toString() : "";
            case "source" -> trip.getSource();
            case "deliverylocation" -> trip.getDeliveryLocation();
            case "notes" -> trip.getNotes();
            default -> "";
        };
    }

    private void applyTripFieldChange(Trip trip, String fieldName, String newValue) {
        switch (fieldName.toLowerCase()) {
            case "quantity" -> {
                BigDecimal qty = new BigDecimal(newValue);
                trip.setQuantity(qty);
                if (trip.getAppliedRate() != null && !Boolean.TRUE.equals(trip.getIsNoLoad())) {
                    trip.setTotalAmount(qty.multiply(trip.getAppliedRate()));
                }
            }
            case "material" -> trip.setMaterial(newValue);
            case "appliedrate", "rate" -> {
                BigDecimal rate = new BigDecimal(newValue);
                trip.setAppliedRate(rate);
                if (trip.getQuantity() != null && !Boolean.TRUE.equals(trip.getIsNoLoad())) {
                    trip.setTotalAmount(trip.getQuantity().multiply(rate));
                }
            }
            case "source" -> trip.setSource(newValue);
            case "deliverylocation" -> trip.setDeliveryLocation(newValue);
            case "notes" -> trip.setNotes(newValue);
        }
    }
}
