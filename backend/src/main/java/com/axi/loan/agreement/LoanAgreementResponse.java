package com.axi.loan.agreement;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record LoanAgreementResponse(
        Long id,
        String agreementNumber,
        Long applicationId,
        Long clientId,
        String clientFullName,
        String clientPhone,
        String clientPassport,
        String purpose,
        BigDecimal requestedAmount,
        BigDecimal approvedAmount,
        Short termMonths,
        OffsetDateTime signedAt,
        SignatureStatus signatureStatus
) {
    static LoanAgreementResponse from(LoanAgreement agreement) {
        var application = agreement.getApplication();
        var client = application.getClient();
        var decision = agreement.getDecision();
        var passport = client.getPassport();
        String fullName = String.join(" ",
                client.getLastName(),
                client.getFirstName(),
                client.getMiddleName() == null ? "" : client.getMiddleName()).trim();
        String passportNumber = passport == null ? null : passport.getSeries() + " " + passport.getNumber();

        return new LoanAgreementResponse(
                agreement.getId(), agreement.getAgreementNumber(), application.getId(), client.getId(),
                fullName, client.getPhone(), passportNumber, application.getPurpose(),
                application.getRequestedAmount(), decision.getApprovedAmount(), decision.getTermMonths(),
                agreement.getSignedAt(), agreement.getSignatureStatus());
    }
}
