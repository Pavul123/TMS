package com.transport.tms.trip.repository;

import com.transport.tms.trip.entity.TripStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripStatusHistoryRepository extends JpaRepository<TripStatusHistory, Long> {
    List<TripStatusHistory> findByTripIdOrderByChangedAtDesc(String tripId);
}
