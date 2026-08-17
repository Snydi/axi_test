package com.axi.loan.application;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanApplicationService {

    private static final int MAX_PAGE_SIZE = 100;
    private final LoanApplicationRepository repository;

    public LoanApplicationService(LoanApplicationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public LoanApplicationPageResponse getApplications(int requestedPage, int requestedSize) {
        int page = Math.max(requestedPage, 0);
        int size = Math.clamp(requestedSize, 1, MAX_PAGE_SIZE);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        var result = repository.findAll(pageable);

        return new LoanApplicationPageResponse(
                result.stream().map(LoanApplicationResponse::from).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }
}
