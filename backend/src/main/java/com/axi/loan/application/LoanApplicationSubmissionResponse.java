package com.axi.loan.application;

import java.math.BigDecimal;

public record LoanApplicationSubmissionResponse(
        Long applicationId,
        LoanDecisionStatus status,
        BigDecimal approvedAmount,
        Short termMonths,
        String agreementNumber,
        String message
) {
}
