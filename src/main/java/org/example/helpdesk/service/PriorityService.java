package org.example.helpdesk.service;

import java.util.List;
import org.example.helpdesk.dto.CreatePriorityRequest;
import org.example.helpdesk.dto.PriorityResponse;

public interface PriorityService {
    PriorityResponse create(CreatePriorityRequest request);

    List<PriorityResponse> findAll();

    PriorityResponse findById(Long id);

    PriorityResponse update(Long id, CreatePriorityRequest request);

    void delete(Long id);
}
