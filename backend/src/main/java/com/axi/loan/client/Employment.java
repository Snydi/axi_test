package com.axi.loan.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "employments")
public class Employment {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "employed_from", nullable = false)
    private LocalDate employedFrom;

    @Column(name = "employed_to")
    private LocalDate employedTo;

    @Column(nullable = false)
    private String position;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    protected Employment() {
    }

    public Long getId() { return id; }
    public Client getClient() { return client; }
    public LocalDate getEmployedFrom() { return employedFrom; }
    public LocalDate getEmployedTo() { return employedTo; }
    public String getPosition() { return position; }
    public String getOrganizationName() { return organizationName; }
}
