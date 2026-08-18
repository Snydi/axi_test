package com.axi.loan.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanDecisionRepository extends JpaRepository<LoanDecision, Long> {
}
