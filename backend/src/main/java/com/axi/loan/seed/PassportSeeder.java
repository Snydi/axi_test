package com.axi.loan.seed;

import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Order(2)
public class PassportSeeder implements Seeder {

    private static final String INSERT_PASSPORT = """
            INSERT INTO passports (
                client_id, series, number, department_code, issued_by, issue_date
            )
            SELECT id, ?, ?, ?, ?, ?
            FROM clients
            WHERE phone = ?
            ON CONFLICT DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    public PassportSeeder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void seed() {
        List<Object[]> passports = List.of(
                new Object[]{"4510", "123456", "770-001", "ГУ МВД России по г. Москве",
                        LocalDate.of(2015, 4, 12), "+79001234567"},
                new Object[]{"4014", "654321", "780-002", "ГУ МВД России по г. Санкт-Петербургу",
                        LocalDate.of(2018, 9, 3), "+79007654321"},
                new Object[]{"9212", "112233", "160-003", "УМВД России по г. Казани",
                        LocalDate.of(2016, 6, 21), "+79005554433"},
                new Object[]{"5016", "224466", "540-004", "УМВД России по г. Новосибирску",
                        LocalDate.of(2019, 2, 14), "+79001112233"},
                new Object[]{"6517", "335577", "660-005", "УМВД России по г. Екатеринбургу",
                        LocalDate.of(2020, 7, 8), "+79002223344"},
                new Object[]{"3613", "446688", "630-006", "УМВД России по г. Самаре",
                        LocalDate.of(2017, 11, 27), "+79003334455"},
                new Object[]{"5215", "557799", "550-007", "УМВД России по г. Омску",
                        LocalDate.of(2018, 5, 19), "+79004445566"},
                new Object[]{"8018", "668800", "020-008", "УМВД России по г. Уфе",
                        LocalDate.of(2021, 3, 6), "+79006667788"},
                new Object[]{"6014", "779911", "610-009", "УМВД России по г. Ростову-на-Дону",
                        LocalDate.of(2016, 12, 10), "+79008889900"},
                new Object[]{"0319", "880022", "230-010", "УМВД России по г. Краснодару",
                        LocalDate.of(2022, 8, 25), "+79009990011"}
        );

        jdbcTemplate.batchUpdate(INSERT_PASSPORT, passports);
    }
}
