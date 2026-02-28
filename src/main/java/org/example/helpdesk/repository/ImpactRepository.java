package org.example.helpdesk.repository;

import org.example.helpdesk.entity.Impact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImpactRepository extends JpaRepository<Impact, Long> {
}
