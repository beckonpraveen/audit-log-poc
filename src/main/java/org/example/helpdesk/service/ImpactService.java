package org.example.helpdesk.service;

import java.util.List;
import org.example.helpdesk.dto.CreateImpactRequest;
import org.example.helpdesk.dto.ImpactResponse;

public interface ImpactService {
    ImpactResponse create(CreateImpactRequest request);

    List<ImpactResponse> findAll();

    ImpactResponse findById(Long id);

    ImpactResponse update(Long id, CreateImpactRequest request);

    void delete(Long id);
}
