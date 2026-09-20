package com.transport.tms.master.rate.service;

import com.transport.tms.common.exception.Exceptions;
import com.transport.tms.master.rate.entity.ConfiguredRate;
import com.transport.tms.master.rate.repository.ConfiguredRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateCardService {

    private final ConfiguredRateRepository rateRepository;

    @Transactional(readOnly = true)
    public List<ConfiguredRate> getAllRates() {
        return rateRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ConfiguredRate> getRatesByCustomer(String customerId) {
        return rateRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public BigDecimal resolveRate(String customerId, String material, String loadingLoc, String deliveryLoc) {
        List<ConfiguredRate> matching = rateRepository.findMatchingRates(customerId, material);
        if (!matching.isEmpty()) {
            // Try exact location match first
            for (ConfiguredRate rate : matching) {
                if (rate.getLoadingLocation().equalsIgnoreCase(loadingLoc) &&
                    rate.getDeliveryLocation().equalsIgnoreCase(deliveryLoc)) {
                    return rate.getRate();
                }
            }
            // Fallback to customer/material default
            return matching.get(0).getRate();
        }
        // Default fallback
        return BigDecimal.valueOf(750.00);
    }

    @Transactional
    public ConfiguredRate createRate(ConfiguredRate rate) {
        if (rate.getId() == null || rate.getId().isBlank()) {
            rate.setId("RAT-" + String.format("%03d", (int)(Math.random() * 900) + 100));
        }
        return rateRepository.save(rate);
    }

    @Transactional
    public ConfiguredRate updateRate(String id, ConfiguredRate request) {
        ConfiguredRate rate = rateRepository.findById(id)
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException("Rate", "id", id));
        rate.setRate(request.getRate());
        rate.setUnit(request.getUnit());
        rate.setStatus(request.getStatus());
        rate.setEffectiveFrom(request.getEffectiveFrom());
        return rateRepository.save(rate);
    }
}
