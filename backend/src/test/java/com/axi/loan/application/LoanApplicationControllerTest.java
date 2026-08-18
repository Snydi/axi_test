package com.axi.loan.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoanApplicationControllerTest {

    private final LoanApplicationService service = mock(LoanApplicationService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new LoanApplicationController(service)).build();
    }

    @Test
    void acceptsAValidLoanApplication() throws Exception {
        var request = TestApplicationRequests.validRequest();
        var response = new LoanApplicationSubmissionResponse(
                42L, LoanDecisionStatus.APPROVED, new BigDecimal("100000"),
                (short) 12, "KD-2026-00000042", "Approved"
        );
        when(service.submitApplication(request)).thenReturn(response);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "lastName": "Ivanov",
                                  "firstName": "Ivan",
                                  "middleName": null,
                                  "passportSeries": "1234",
                                  "passportNumber": "567890",
                                  "passportDepartmentCode": "123-456",
                                  "passportIssuedBy": "Police department",
                                  "passportIssueDate": "2020-01-10",
                                  "gender": "MALE",
                                  "maritalStatus": "SINGLE",
                                  "residentialAddress": "Residential address",
                                  "registrationAddress": "Registration address",
                                  "phone": "+7 (999) 123-45-67",
                                  "employedFrom": "2021-02-01",
                                  "employedTo": null,
                                  "position": "Engineer",
                                  "organizationName": "Axi",
                                  "requestedAmount": 100000,
                                  "purpose": "Home renovation"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.applicationId").value(42))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.agreementNumber").value("KD-2026-00000042"));

        verify(service).submitApplication(request);
    }

    @Test
    void rejectsAnInvalidLoanApplicationBeforeCallingTheService() throws Exception {
        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(service, org.mockito.Mockito.never())
                .submitApplication(org.mockito.ArgumentMatchers.any());
    }
}
