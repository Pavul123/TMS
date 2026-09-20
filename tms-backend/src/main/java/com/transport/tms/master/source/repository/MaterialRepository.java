package com.transport.tms.master.source.repository;

import com.transport.tms.master.source.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, String> {
    List<Material> findByStatus(String status);
}
