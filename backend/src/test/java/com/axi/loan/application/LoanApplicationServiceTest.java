package com.axi.loan.application;

import com.axi.loan.agreement.LoanAgreementRepository;
import com.axi.loan.client.ClientRepository;
import com.axi.loan.client.EmploymentRepository;
import com.axi.loan.client.PassportRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoanApplicationServiceTest {

    private final LoanApplicationRepository applicationRepository = mock(LoanApplicationRepository.class);
    private final ClientRepository clientRepository = mock(ClientRepository.class);
    private final PassportRepository passportRepository = mock(PassportRepository.class);
    private final EmploymentRepository employmentRepository = mock(EmploymentRepository.class);
    private final LoanDecisionRepository decisionRepository = mock(LoanDecisionRepository.class);
    private final LoanAgreementRepository agreementRepository = mock(LoanAgreementRepository.class);
    private final LoanApplicationService service = new LoanApplicationService(
            applicationRepository,
            clientRepository,
            passportRepository,
            employmentRepository,
            decisionRepository,
            agreementRepository
    );

    @Test
    void rejectsAnApplicationWhenThePhoneAlreadyBelongsToAClient() {
        var request = TestApplicationRequests.validRequest();
        when(clientRepository.existsByPhone("+79991234567")).thenReturn(true);

        var exception = assertThrows(
                ApplicationSubmissionConflictException.class,
                () -> service.submitApplication(request)
        );

        assertEquals("phone", exception.getField());
        verify(clientRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(applicationRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
