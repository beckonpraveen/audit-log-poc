package org.example.helpdesk.service;

import java.util.List;
import org.example.helpdesk.dto.CreateImpactRequest;
import org.example.helpdesk.dto.ImpactResponse;
import org.example.helpdesk.entity.Impact;
import org.example.helpdesk.repository.ImpactRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImpactServiceImpl implements ImpactService {

    private final ImpactRepository impactRepository;

    public ImpactServiceImpl(ImpactRepository impactRepository) {
        this.impactRepository = impactRepository;
    }

    @Override
    public ImpactResponse create(CreateImpactRequest request) {
        Impact impact = new Impact();
        apply(impact, request);
        return toResponse(impactRepository.save(impact));
    }

    @Override
    public List<ImpactResponse> findAll() {
        return impactRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public ImpactResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Override
    public ImpactResponse update(Long id, CreateImpactRequest request) {
        Impact impact = findEntityById(id);
        apply(impact, request);
        return toResponse(impactRepository.save(impact));
    }

    @Override
    public void delete(Long id) {
        Impact impact = findEntityById(id);
        impactRepository.delete(impact);
    }

    protected Impact findEntityById(Long id) {
        return impactRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Impact not found: " + id));
    }

    private void apply(Impact impact, CreateImpactRequest request) {
        impact.setName(request.getName());
        impact.setDescription(request.getDescription());
        impact.setActive(request.getActive() == null || request.getActive());
    }

    private ImpactResponse toResponse(Impact impact) {
        ImpactResponse response = new ImpactResponse();
        response.setId(impact.getId());
        response.setName(impact.getName());
        response.setDescription(impact.getDescription());
        response.setActive(impact.isActive());
        return response;
    }
}
