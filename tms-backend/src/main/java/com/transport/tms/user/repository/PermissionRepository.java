package com.transport.tms.user.repository;

import com.transport.tms.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    List<Permission> findByModule(String module);
}
