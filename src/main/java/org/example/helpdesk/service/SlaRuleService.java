package org.example.helpdesk.service;

import java.util.List;
import org.example.helpdesk.dto.CreateSlaRuleRequest;
import org.example.helpdesk.dto.SlaRuleResponse;
import org.example.helpdesk.entity.SlaRule;

public interface SlaRuleService {
    SlaRuleResponse create(CreateSlaRuleRequest request);

    List<SlaRuleResponse> findAll(Boolean active);

    SlaRuleResponse findById(Long id);

    SlaRuleResponse update(Long id, CreateSlaRuleRequest request);

    void delete(Long id);

    SlaRule findMatchingForPriority(Long priorityId);
}
