package com.axi.loan.seed;

import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@Order(6)
public class LoanAgreementSeeder implements Seeder {

    private static final String INSERT_AGREEMENT = """
            INSERT INTO loan_agreements (
                application_id, decision_id, agreement_number, signature_status,
                signed_at, created_at, updated_at
            )
            SELECT application.id, decision.id, ?, CAST(? AS signature_status_type), ?, ?, ?
            FROM loan_applications application
            JOIN clients client ON client.id = application.client_id
            JOIN loan_decisions decision ON decision.application_id = application.id
            WHERE client.phone = ? AND decision.status = 'APPROVED'
            ON CONFLICT (application_id) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    public LoanAgreementSeeder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void seed() {
        OffsetDateTime createdAt = OffsetDateTime.of(2026, 2, 4, 10, 0, 0, 0, ZoneOffset.UTC);
        List<AgreementSeed> agreements = List.of(
                new AgreementSeed("+79001234567", "КД-2026-0001", "SIGNED", createdAt.plusDays(1)),
                new AgreementSeed("+79007654321", "КД-2026-0002", "SIGNED", createdAt.plusDays(2)),
                new AgreementSeed("+79001112233", "КД-2026-0003", "SIGNED", createdAt.plusDays(4)),
                new AgreementSeed("+79004445566", "КД-2026-0004", "SIGNED", createdAt.plusDays(7)),
                new AgreementSeed("+79006667788", "КД-2026-0005", "SIGNED", createdAt.plusDays(8)),
                new AgreementSeed("+79009990011", "КД-2026-0006", "UNSIGNED", null)
        );

        for (var agreement : agreements) {
            jdbcTemplate.update(INSERT_AGREEMENT,
                    agreement.number(), agreement.status(), agreement.signedAt(), createdAt, createdAt,
                    agreement.phone());
        }
    }

    private record AgreementSeed(
            String phone,
            String number,
            String status,
            OffsetDateTime signedAt
    ) {
    }
}
