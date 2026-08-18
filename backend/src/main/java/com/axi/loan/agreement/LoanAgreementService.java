package com.axi.loan.agreement;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class LoanAgreementService {

    private static final int MAX_PAGE_SIZE = 100;
    private final LoanAgreementRepository repository;

    public LoanAgreementService(LoanAgreementRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public LoanAgreementPageResponse getAgreements(int requestedPage, int requestedSize) {
        int page = Math.max(requestedPage, 0);
        int size = Math.clamp(requestedSize, 1, MAX_PAGE_SIZE);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        var result = repository.findAll(pageable);

        return new LoanAgreementPageResponse(
                result.stream().map(LoanAgreementResponse::from).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Transactional
    public LoanAgreementResponse signAgreement(Long agreementId) {
        var agreement = repository.findById(agreementId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Договор не найден"));

        agreement.sign(OffsetDateTime.now(ZoneOffset.UTC));
        return LoanAgreementResponse.from(agreement);
    }
}
