package com.axi.loan.agreement;

import java.util.List;

public record LoanAgreementPageResponse(
        List<LoanAgreementResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
