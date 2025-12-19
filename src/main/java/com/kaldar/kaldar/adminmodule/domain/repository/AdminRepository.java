package com.kaldar.kaldar.adminmodule.domain.repository;

import com.kaldar.kaldar.adminmodule.domain.model.Admins;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admins, Long> {
}
