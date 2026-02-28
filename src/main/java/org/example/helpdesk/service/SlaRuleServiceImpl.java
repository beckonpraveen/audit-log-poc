package org.example.helpdesk.service;

import java.util.List;
import org.example.helpdesk.dto.CreateSlaRuleRequest;
import org.example.helpdesk.dto.SlaRuleResponse;
import org.example.helpdesk.entity.Priority;
import org.example.helpdesk.entity.SlaRule;
import org.example.helpdesk.repository.SlaRuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SlaRuleServiceImpl implements SlaRuleService {

    private final SlaRuleRepository slaRuleRepository;
    private final PriorityServiceImpl priorityService;

    public SlaRuleServiceImpl(SlaRuleRepository slaRuleRepository, PriorityServiceImpl priorityService) {
        this.slaRuleRepository = slaRuleRepository;
        this.priorityService = priorityService;
    }

    @Override
    public SlaRuleResponse create(CreateSlaRuleRequest request) {
        Priority priority = priorityService.findEntityById(request.getPriorityId());
        SlaRule rule = new SlaRule();
        apply(rule, request, priority);
        return toResponse(slaRuleRepository.save(rule));
    }

    @Override
    public List<SlaRuleResponse> findAll(Boolean active) {
        List<SlaRule> rules = active == null
                ? slaRuleRepository.findAll()
                : (active ? slaRuleRepository.findByActiveTrueOrderBySortOrderAscIdAsc() : slaRuleRepository.findAll().stream().filter(r -> !r.isActive()).toList());
        return rules.stream().map(this::toResponse).toList();
    }

    @Override
    public SlaRuleResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Override
    public SlaRuleResponse update(Long id, CreateSlaRuleRequest request) {
        SlaRule rule = findEntityById(id);
        Priority priority = priorityService.findEntityById(request.getPriorityId());
        apply(rule, request, priority);
        return toResponse(slaRuleRepository.save(rule));
    }

    @Override
    public void delete(Long id) {
        SlaRule rule = findEntityById(id);
        slaRuleRepository.delete(rule);
    }

    @Override
    public SlaRule findMatchingForPriority(Long priorityId) {
        if (priorityId == null) {
            return null;
        }
        return slaRuleRepository.findFirstByActiveTrueAndPriorityIdOrderBySortOrderAscIdAsc(priorityId).orElse(null);
    }

    protected SlaRule findEntityById(Long id) {
        return slaRuleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SLA rule not found: " + id));
    }

    private void apply(SlaRule rule, CreateSlaRuleRequest request, Priority priority) {
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        rule.setPriority(priority);
        rule.setResponseTimeMinutes(request.getResponseTimeMinutes());
        rule.setResolutionTimeMinutes(request.getResolutionTimeMinutes());
        rule.setSortOrder(request.getSortOrder() == null ? 100 : request.getSortOrder());
        rule.setActive(request.getActive() == null || request.getActive());
    }

    private SlaRuleResponse toResponse(SlaRule rule) {
        SlaRuleResponse response = new SlaRuleResponse();
        response.setId(rule.getId());
        response.setName(rule.getName());
        response.setDescription(rule.getDescription());
        response.setPriorityId(rule.getPriority().getId());
        response.setPriorityName(rule.getPriority().getName());
        response.setResponseTimeMinutes(rule.getResponseTimeMinutes());
        response.setResolutionTimeMinutes(rule.getResolutionTimeMinutes());
        response.setSortOrder(rule.getSortOrder());
        response.setActive(rule.isActive());
        return response;
    }
}
