package com.transport.tms.master.source.repository;

import com.transport.tms.master.source.entity.LocationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<LocationItem, String> {
    List<LocationItem> findByStatus(String status);
}
