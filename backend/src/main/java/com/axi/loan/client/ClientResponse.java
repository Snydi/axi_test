package com.axi.loan.client;

import java.time.OffsetDateTime;
import java.util.List;

public record ClientResponse(
        Long id,
        String firstName,
        String lastName,
        String middleName,
        String fullName,
        String phone,
        String passport,
        Gender gender,
        MaritalStatus maritalStatus,
        String residentialAddress,
        String registrationAddress,
        List<EmploymentResponse> employments,
        OffsetDateTime createdAt
) {
    static ClientResponse from(Client client) {
        String fullName = String.join(" ",
                client.getLastName(),
                client.getFirstName(),
                client.getMiddleName() == null ? "" : client.getMiddleName()).trim();
        String passport = client.getPassport() == null
                ? null
                : client.getPassport().getSeries() + " " + client.getPassport().getNumber();
        List<EmploymentResponse> employments = client.getEmployments().stream()
                .map(EmploymentResponse::from)
                .toList();

        return new ClientResponse(
                client.getId(), client.getFirstName(), client.getLastName(), client.getMiddleName(),
                fullName, client.getPhone(), passport, client.getGender(), client.getMaritalStatus(),
                client.getResidentialAddress(), client.getRegistrationAddress(), employments,
                client.getCreatedAt());
    }
}
