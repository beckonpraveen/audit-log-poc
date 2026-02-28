package org.example.helpdesk.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.helpdesk.dto.CreateSlaRuleRequest;
import org.example.helpdesk.dto.SlaRuleResponse;
import org.example.helpdesk.service.SlaRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sla-rules")
public class SlaRuleController {

    private final SlaRuleService slaRuleService;

    public SlaRuleController(SlaRuleService slaRuleService) {
        this.slaRuleService = slaRuleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SlaRuleResponse create(@Valid @RequestBody CreateSlaRuleRequest request) {
        return slaRuleService.create(request);
    }

    @GetMapping
    public List<SlaRuleResponse> findAll(@RequestParam(required = false) Boolean active) {
        return slaRuleService.findAll(active);
    }

    @GetMapping("/{id}")
    public SlaRuleResponse findById(@PathVariable Long id) {
        return slaRuleService.findById(id);
    }

    @PutMapping("/{id}")
    public SlaRuleResponse update(@PathVariable Long id, @Valid @RequestBody CreateSlaRuleRequest request) {
        return slaRuleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        slaRuleService.delete(id);
    }
}
