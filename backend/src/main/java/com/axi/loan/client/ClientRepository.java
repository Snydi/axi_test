package com.axi.loan.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {

    boolean existsByPhone(String phone);

    @Override
    @EntityGraph(attributePaths = "passport")
    Page<Client> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "passport")
    Page<Client> findAll(Specification<Client> specification, Pageable pageable);
}
