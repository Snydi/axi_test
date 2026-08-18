package com.axi.loan.application;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "loan_decisions")
public class LoanDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private LoanApplication application;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "loan_decision_status")
    private LoanDecisionStatus status;

    @Column(name = "approved_amount")
    private BigDecimal approvedAmount;

    @Column(name = "term_months")
    private Short termMonths;

    @Column(name = "decided_at")
    private OffsetDateTime decidedAt;

    protected LoanDecision() {
    }

    public LoanDecision(
            LoanApplication application,
            LoanDecisionStatus status,
            BigDecimal approvedAmount,
            Short termMonths,
            OffsetDateTime decidedAt
    ) {
        this.application = application;
        this.status = status;
        this.approvedAmount = approvedAmount;
        this.termMonths = termMonths;
        this.decidedAt = decidedAt;
    }

    public Long getId() { return id; }

    public LoanDecisionStatus getStatus() { return status; }
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public Short getTermMonths() { return termMonths; }
    public OffsetDateTime getDecidedAt() { return decidedAt; }
}
