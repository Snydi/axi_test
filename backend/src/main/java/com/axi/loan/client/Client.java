package com.axi.loan.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(nullable = false)
    private String phone;

    @Column(name = "residential_address", nullable = false)
    private String residentialAddress;

    @Column(name = "registration_address", nullable = false)
    private String registrationAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "gender_type")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", nullable = false, columnDefinition = "marital_status_type")
    private MaritalStatus maritalStatus;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToOne(mappedBy = "client", fetch = FetchType.LAZY)
    private Passport passport;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    @OrderBy("employedFrom DESC")
    private List<Employment> employments = new ArrayList<>();

    protected Client() {
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMiddleName() { return middleName; }
    public String getPhone() { return phone; }
    public String getResidentialAddress() { return residentialAddress; }
    public String getRegistrationAddress() { return registrationAddress; }
    public Gender getGender() { return gender; }
    public MaritalStatus getMaritalStatus() { return maritalStatus; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public Passport getPassport() { return passport; }
    public List<Employment> getEmployments() { return employments; }
}
