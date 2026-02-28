package org.example.helpdesk.service;

import java.util.List;
import org.example.helpdesk.dto.CreatePriorityRequest;
import org.example.helpdesk.dto.PriorityResponse;
import org.example.helpdesk.entity.Priority;
import org.example.helpdesk.repository.PriorityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PriorityServiceImpl implements PriorityService {

    private final PriorityRepository priorityRepository;

    public PriorityServiceImpl(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    @Override
    public PriorityResponse create(CreatePriorityRequest request) {
        Priority priority = new Priority();
        apply(priority, request);
        return toResponse(priorityRepository.save(priority));
    }

    @Override
    public List<PriorityResponse> findAll() {
        return priorityRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public PriorityResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Override
    public PriorityResponse update(Long id, CreatePriorityRequest request) {
        Priority priority = findEntityById(id);
        apply(priority, request);
        return toResponse(priorityRepository.save(priority));
    }

    @Override
    public void delete(Long id) {
        Priority priority = findEntityById(id);
        priorityRepository.delete(priority);
    }

    protected Priority findEntityById(Long id) {
        return priorityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Priority not found: " + id));
    }

    private void apply(Priority priority, CreatePriorityRequest request) {
        priority.setName(request.getName());
        priority.setDescription(request.getDescription());
        priority.setActive(request.getActive() == null || request.getActive());
    }

    private PriorityResponse toResponse(Priority priority) {
        PriorityResponse response = new PriorityResponse();
        response.setId(priority.getId());
        response.setName(priority.getName());
        response.setDescription(priority.getDescription());
        response.setActive(priority.isActive());
        return response;
    }
}
