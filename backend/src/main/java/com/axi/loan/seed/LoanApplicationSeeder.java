package com.axi.loan.seed;

import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@Order(4)
public class LoanApplicationSeeder implements Seeder {

    private static final String INSERT_APPLICATION = """
            INSERT INTO loan_applications (
                client_id, requested_amount, purpose, created_at, updated_at
            )
            SELECT client.id, ?, ?, ?, ?
            FROM clients client
            WHERE client.phone = ?
              AND NOT EXISTS (
                  SELECT 1 FROM loan_applications application
                  WHERE application.client_id = client.id AND application.purpose = ?
              )
            """;

    private final JdbcTemplate jdbcTemplate;

    public LoanApplicationSeeder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void seed() {
        OffsetDateTime createdAt = OffsetDateTime.of(2026, 2, 2, 9, 0, 0, 0, ZoneOffset.UTC);
        List<ApplicationSeed> applications = List.of(
                new ApplicationSeed("+79001234567", new BigDecimal("850000.00"), "Ремонт квартиры"),
                new ApplicationSeed("+79007654321", new BigDecimal("420000.00"), "Покупка автомобиля"),
                new ApplicationSeed("+79005554433", new BigDecimal("180000.00"), "Оплата обучения"),
                new ApplicationSeed("+79001112233", new BigDecimal("600000.00"), "Ремонт дома"),
                new ApplicationSeed("+79002223344", new BigDecimal("250000.00"), "Медицинские услуги"),
                new ApplicationSeed("+79003334455", new BigDecimal("950000.00"), "Покупка автомобиля"),
                new ApplicationSeed("+79004445566", new BigDecimal("300000.00"), "Развитие личного хозяйства"),
                new ApplicationSeed("+79006667788", new BigDecimal("120000.00"), "Путешествие"),
                new ApplicationSeed("+79008889900", new BigDecimal("700000.00"), "Ремонт квартиры"),
                new ApplicationSeed("+79009990011", new BigDecimal("210000.00"), "Покупка бытовой техники")
        );

        for (int index = 0; index < applications.size(); index++) {
            var application = applications.get(index);
            var timestamp = createdAt.plusDays(index);
            jdbcTemplate.update(INSERT_APPLICATION,
                    application.amount(), application.purpose(), timestamp, timestamp,
                    application.phone(), application.purpose());
        }
    }

    private record ApplicationSeed(String phone, BigDecimal amount, String purpose) {
    }
}
