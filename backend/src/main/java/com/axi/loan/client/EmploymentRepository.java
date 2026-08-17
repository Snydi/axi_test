package com.axi.loan.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {

    List<Employment> findAllByClientIdInOrderByEmployedFromDesc(Collection<Long> clientIds);
}
