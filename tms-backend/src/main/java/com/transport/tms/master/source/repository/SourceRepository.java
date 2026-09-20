package com.transport.tms.master.source.repository;

import com.transport.tms.master.source.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceRepository extends JpaRepository<Source, String> {
    List<Source> findByStatus(String status);
}
