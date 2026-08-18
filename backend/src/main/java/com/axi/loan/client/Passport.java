package com.axi.loan.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "passports")
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false)
    private String series;

    @Column(nullable = false)
    private String number;

    @Column(name = "department_code", nullable = false)
    private String departmentCode;

    @Column(name = "issued_by", nullable = false)
    private String issuedBy;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    protected Passport() {
    }

    public Passport(
            Client client,
            String series,
            String number,
            String departmentCode,
            String issuedBy,
            LocalDate issueDate
    ) {
        this.client = client;
        this.series = series;
        this.number = number;
        this.departmentCode = departmentCode;
        this.issuedBy = issuedBy;
        this.issueDate = issueDate;
    }

    public String getSeries() { return series; }
    public String getNumber() { return number; }
    public String getDepartmentCode() { return departmentCode; }
    public String getIssuedBy() { return issuedBy; }
    public LocalDate getIssueDate() { return issueDate; }
}
