package org.example.helpdesk.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.helpdesk.dto.CreatePriorityRequest;
import org.example.helpdesk.dto.PriorityResponse;
import org.example.helpdesk.service.PriorityService;
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
@RequestMapping("/api/priorities")
public class PriorityController {

    private final PriorityService priorityService;

    public PriorityController(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PriorityResponse create(@Valid @RequestBody CreatePriorityRequest request) {
        return priorityService.create(request);
    }

    @GetMapping
    public List<PriorityResponse> findAll() {
        return priorityService.findAll();
    }

    @GetMapping("/{id}")
    public PriorityResponse findById(@PathVariable Long id) {
        return priorityService.findById(id);
    }

    @PutMapping("/{id}")
    public PriorityResponse update(@PathVariable Long id, @Valid @RequestBody CreatePriorityRequest request) {
        return priorityService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        priorityService.delete(id);
    }
}
