package com.axi.loan.seed;

import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@Order(5)
public class LoanDecisionSeeder implements Seeder {

    private static final String INSERT_DECISION = """
            INSERT INTO loan_decisions (
                application_id, status, approved_amount, term_months,
                decided_at, created_at, updated_at
            )
            SELECT application.id, CAST(? AS loan_decision_status), ?, ?, ?, ?, ?
            FROM loan_applications application
            JOIN clients client ON client.id = application.client_id
            WHERE client.phone = ? AND application.purpose = ?
            ON CONFLICT (application_id) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    public LoanDecisionSeeder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void seed() {
        OffsetDateTime decidedAt = OffsetDateTime.of(2026, 2, 3, 11, 30, 0, 0, ZoneOffset.UTC);
        List<DecisionSeed> decisions = List.of(
                new DecisionSeed("+79001234567", "Ремонт квартиры", "APPROVED", new BigDecimal("800000.00"), (short) 12),
                new DecisionSeed("+79007654321", "Покупка автомобиля", "APPROVED", new BigDecimal("420000.00"), (short) 10),
                new DecisionSeed("+79005554433", "Оплата обучения", "DENIED", null, null),
                new DecisionSeed("+79001112233", "Ремонт дома", "APPROVED", new BigDecimal("550000.00"), (short) 9),
                new DecisionSeed("+79002223344", "Медицинские услуги", "PENDING", null, null),
                new DecisionSeed("+79003334455", "Покупка автомобиля", "DENIED", null, null),
                new DecisionSeed("+79004445566", "Развитие личного хозяйства", "APPROVED", new BigDecimal("275000.00"), (short) 8),
                new DecisionSeed("+79006667788", "Путешествие", "APPROVED", new BigDecimal("100000.00"), (short) 4),
                new DecisionSeed("+79008889900", "Ремонт квартиры", "PENDING", null, null),
                new DecisionSeed("+79009990011", "Покупка бытовой техники", "APPROVED", new BigDecimal("210000.00"), (short) 6)
        );

        for (int index = 0; index < decisions.size(); index++) {
            var decision = decisions.get(index);
            OffsetDateTime decisionTime = "PENDING".equals(decision.status()) ? null : decidedAt.plusDays(index);
            OffsetDateTime createdAt = decidedAt.minusDays(1).plusDays(index);
            jdbcTemplate.update(INSERT_DECISION,
                    decision.status(), decision.approvedAmount(), decision.termMonths(), decisionTime,
                    createdAt, createdAt, decision.phone(), decision.purpose());
        }
    }

    private record DecisionSeed(
            String phone,
            String purpose,
            String status,
            BigDecimal approvedAmount,
            Short termMonths
    ) {
    }
}
