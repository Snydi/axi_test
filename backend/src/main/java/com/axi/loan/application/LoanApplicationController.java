package com.axi.loan.application;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applications")
public class LoanApplicationController {

    private final LoanApplicationService service;

    public LoanApplicationController(LoanApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public LoanApplicationPageResponse getApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size
    ) {
        return service.getApplications(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanApplicationSubmissionResponse submitApplication(
            @Valid @RequestBody LoanApplicationSubmissionRequest request
    ) {
        return service.submitApplication(request);
    }
}
