package com.axi.loan.client;

import java.time.LocalDate;

public record EmploymentResponse(
        Long id,
        LocalDate employedFrom,
        LocalDate employedTo,
        String position,
        String organizationName
) {
    static EmploymentResponse from(Employment employment) {
        return new EmploymentResponse(
                employment.getId(),
                employment.getEmployedFrom(),
                employment.getEmployedTo(),
                employment.getPosition(),
                employment.getOrganizationName()
        );
    }
}
