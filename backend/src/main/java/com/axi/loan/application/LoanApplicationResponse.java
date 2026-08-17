package com.axi.loan.application;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record LoanApplicationResponse(
        Long id,
        Long clientId,
        String clientFullName,
        String clientPhone,
        BigDecimal requestedAmount,
        BigDecimal approvedAmount,
        Short termMonths,
        String purpose,
        LoanDecisionStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime decidedAt
) {
    static LoanApplicationResponse from(LoanApplication application) {
        var client = application.getClient();
        var decision = application.getDecision();
        String fullName = String.join(" ",
                client.getLastName(),
                client.getFirstName(),
                client.getMiddleName() == null ? "" : client.getMiddleName()).trim();

        return new LoanApplicationResponse(
                application.getId(), client.getId(), fullName, client.getPhone(),
                application.getRequestedAmount(), decision == null ? null : decision.getApprovedAmount(),
                decision == null ? null : decision.getTermMonths(), application.getPurpose(),
                decision == null ? LoanDecisionStatus.PENDING : decision.getStatus(),
                application.getCreatedAt(), decision == null ? null : decision.getDecidedAt());
    }
}
