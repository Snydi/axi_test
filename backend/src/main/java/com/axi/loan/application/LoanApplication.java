package com.axi.loan.application;

import com.axi.loan.client.Client;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "requested_amount", nullable = false)
    private BigDecimal requestedAmount;

    @Column(nullable = false)
    private String purpose;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToOne(mappedBy = "application", fetch = FetchType.LAZY)
    private LoanDecision decision;

    protected LoanApplication() {
    }

    public Long getId() { return id; }
    public Client getClient() { return client; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public String getPurpose() { return purpose; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public LoanDecision getDecision() { return decision; }
}
