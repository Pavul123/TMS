package com.transport.tms.trip.repository;

import com.transport.tms.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, String> {

    List<Trip> findByDate(LocalDate date);

    List<Trip> findByCustomerId(String customerId);

    List<Trip> findByVehicleRegistration(String vehicleRegistration);

    List<Trip> findByDriverId(String driverId);

    List<Trip> findByStatus(String status);

    @Query("SELECT t FROM Trip t WHERE t.customerId = :customerId AND t.invoiceId IS NULL AND t.isNoLoad = false")
    List<Trip> findUnbilledTripsByCustomer(@Param("customerId") String customerId);

    @Query("SELECT t FROM Trip t WHERE t.invoiceId IS NULL AND t.isNoLoad = false")
    List<Trip> findAllUnbilledTrips();

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.invoiceId IS NULL AND t.isNoLoad = false")
    long countUnbilledTrips();
}
