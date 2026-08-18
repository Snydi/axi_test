package com.axi.loan.agreement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanAgreementRepository extends JpaRepository<LoanAgreement, Long> {

    @Override
    @EntityGraph(attributePaths = {"application.client.passport", "decision"})
    Page<LoanAgreement> findAll(Pageable pageable);
}
