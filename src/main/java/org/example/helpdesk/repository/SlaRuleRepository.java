package org.example.helpdesk.repository;

import java.util.List;
import java.util.Optional;
import org.example.helpdesk.entity.SlaRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlaRuleRepository extends JpaRepository<SlaRule, Long> {
    List<SlaRule> findByActiveTrueOrderBySortOrderAscIdAsc();

    Optional<SlaRule> findFirstByActiveTrueAndPriorityIdOrderBySortOrderAscIdAsc(Long priorityId);
}
