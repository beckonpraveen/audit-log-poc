package org.example.helpdesk.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.helpdesk.dto.CreateImpactRequest;
import org.example.helpdesk.dto.ImpactResponse;
import org.example.helpdesk.service.ImpactService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/impacts")
public class ImpactController {

    private final ImpactService impactService;

    public ImpactController(ImpactService impactService) {
        this.impactService = impactService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ImpactResponse create(@Valid @RequestBody CreateImpactRequest request) {
        return impactService.create(request);
    }

    @GetMapping
    public List<ImpactResponse> findAll() {
        return impactService.findAll();
    }

    @GetMapping("/{id}")
    public ImpactResponse findById(@PathVariable Long id) {
        return impactService.findById(id);
    }

    @PutMapping("/{id}")
    public ImpactResponse update(@PathVariable Long id, @Valid @RequestBody CreateImpactRequest request) {
        return impactService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        impactService.delete(id);
    }
}
