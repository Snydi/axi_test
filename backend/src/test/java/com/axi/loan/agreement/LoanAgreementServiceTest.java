package com.axi.loan.agreement;

import com.axi.loan.application.LoanApplication;
import com.axi.loan.application.LoanDecision;
import com.axi.loan.application.LoanDecisionStatus;
import com.axi.loan.client.Client;
import com.axi.loan.client.Gender;
import com.axi.loan.client.MaritalStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoanAgreementServiceTest {

    @Test
    void signsAnApprovedLoanAgreement() {
        var repository = mock(LoanAgreementRepository.class);
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var client = new Client(
                "Ivan", "Ivanov", null, "+79991234567", "Home", "Home",
                Gender.MALE, MaritalStatus.SINGLE, now
        );
        var application = new LoanApplication(client, new BigDecimal("100000"), "Renovation", now);
        var decision = new LoanDecision(
                application, LoanDecisionStatus.APPROVED, new BigDecimal("100000"), (short) 12, now
        );
        var agreement = new LoanAgreement(application, decision, "KD-2026-00000001");
        ReflectionTestUtils.setField(client, "id", 1L);
        ReflectionTestUtils.setField(application, "id", 2L);
        ReflectionTestUtils.setField(agreement, "id", 3L);
        when(repository.findById(3L)).thenReturn(Optional.of(agreement));

        var result = new LoanAgreementService(repository).signAgreement(3L);

        assertEquals(SignatureStatus.SIGNED, result.signatureStatus());
        assertNotNull(result.signedAt());
        assertEquals("KD-2026-00000001", result.agreementNumber());
    }
}
