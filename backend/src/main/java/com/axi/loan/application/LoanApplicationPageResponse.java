package com.axi.loan.application;

import java.util.List;

public record LoanApplicationPageResponse(
        List<LoanApplicationResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
