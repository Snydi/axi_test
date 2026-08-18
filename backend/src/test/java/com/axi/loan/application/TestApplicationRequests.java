package com.axi.loan.application;

import com.axi.loan.client.Gender;
import com.axi.loan.client.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

final class TestApplicationRequests {

    private TestApplicationRequests() {
    }

    static LoanApplicationSubmissionRequest validRequest() {
        return new LoanApplicationSubmissionRequest(
                "Ivanov", "Ivan", null,
                "1234", "567890", "123-456", "Police department",
                LocalDate.of(2020, 1, 10), Gender.MALE, MaritalStatus.SINGLE,
                "Residential address", "Registration address", "+7 (999) 123-45-67",
                LocalDate.of(2021, 2, 1), null, "Engineer", "Axi",
                new BigDecimal("100000"), "Home renovation"
        );
    }
}
