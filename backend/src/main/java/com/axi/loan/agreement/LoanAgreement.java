package com.axi.loan.agreement;

import com.axi.loan.application.LoanApplication;
import com.axi.loan.application.LoanDecision;
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

import java.time.OffsetDateTime;

@Entity
@Table(name = "loan_agreements")
public class LoanAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private LoanApplication application;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_id", nullable = false)
    private LoanDecision decision;

    @Column(name = "agreement_number", nullable = false, unique = true)
    private String agreementNumber;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "signature_status", nullable = false, columnDefinition = "signature_status_type")
    private SignatureStatus signatureStatus;

    @Column(name = "signed_at")
    private OffsetDateTime signedAt;

    protected LoanAgreement() {
    }

    public LoanAgreement(
            LoanApplication application,
            LoanDecision decision,
            String agreementNumber
    ) {
        this.application = application;
        this.decision = decision;
        this.agreementNumber = agreementNumber;
        this.signatureStatus = SignatureStatus.UNSIGNED;
        this.signedAt = null;
    }

    public void sign(OffsetDateTime signedAt) {
        if (signatureStatus == SignatureStatus.SIGNED) {
            return;
        }
        this.signatureStatus = SignatureStatus.SIGNED;
        this.signedAt = signedAt;
    }

    public Long getId() { return id; }
    public LoanApplication getApplication() { return application; }
    public LoanDecision getDecision() { return decision; }
    public String getAgreementNumber() { return agreementNumber; }
    public SignatureStatus getSignatureStatus() { return signatureStatus; }
    public OffsetDateTime getSignedAt() { return signedAt; }
}
