package com.axi.loan.agreement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/agreements", "/agreements"})
public class LoanAgreementController {

    private final LoanAgreementService service;

    public LoanAgreementController(LoanAgreementService service) {
        this.service = service;
    }

    @GetMapping
    public LoanAgreementPageResponse getAgreements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size
    ) {
        return service.getAgreements(page, size);
    }
}
