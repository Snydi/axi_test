package com.axi.loan.seed;

import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Order(3)
public class EmploymentSeeder implements Seeder {

    private static final String INSERT_EMPLOYMENT = """
            INSERT INTO employments (
                client_id, employed_from, employed_to, position, organization_name
            )
            SELECT client.id, ?, ?, ?, ?
            FROM clients client
            WHERE client.phone = ?
              AND NOT EXISTS (
                  SELECT 1
                  FROM employments employment
                  WHERE employment.client_id = client.id
                    AND employment.employed_from = ?
                    AND employment.position = ?
                    AND employment.organization_name = ?
              )
            """;

    private final JdbcTemplate jdbcTemplate;

    public EmploymentSeeder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void seed() {
        List<EmploymentSeed> employments = List.of(
                new EmploymentSeed("+79001234567", LocalDate.of(2018, 3, 1), null,
                        "Ведущий инженер-программист", "AXI Технологии"),
                new EmploymentSeed("+79007654321", LocalDate.of(2021, 5, 10), null,
                        "Финансовый аналитик", "Балтик Финанс"),
                new EmploymentSeed("+79005554433", LocalDate.of(2019, 11, 18), null,
                        "Операционный менеджер", "Волга Логистика"),
                new EmploymentSeed("+79001112233", LocalDate.of(2020, 2, 3), null,
                        "Продуктовый дизайнер", "Сибирь Диджитал"),
                new EmploymentSeed("+79002223344", LocalDate.of(2022, 6, 15), null,
                        "Инженер-строитель", "Урал Строй"),
                new EmploymentSeed("+79003334455", LocalDate.of(2017, 10, 9), null,
                        "Юрисконсульт", "Самарская юридическая группа"),
                new EmploymentSeed("+79004445566", LocalDate.of(2016, 4, 20), null,
                        "Начальник производства", "Омское производство"),
                new EmploymentSeed("+79006667788", LocalDate.of(2023, 1, 16), null,
                        "Менеджер по маркетингу", "Башкирские Медиа"),
                new EmploymentSeed("+79008889900", LocalDate.of(2018, 8, 6), null,
                        "Директор по продажам", "Дон Трейд"),
                new EmploymentSeed("+79009990011", LocalDate.of(2021, 9, 13), null,
                        "Специалист по кадрам", "Кубань Сервис")
        );

        List<Object[]> parameters = employments.stream()
                .map(employment -> new Object[]{
                        employment.employedFrom(), employment.employedTo(), employment.position(),
                        employment.organizationName(), employment.phone(), employment.employedFrom(),
                        employment.position(), employment.organizationName()
                })
                .toList();

        jdbcTemplate.batchUpdate(INSERT_EMPLOYMENT, parameters);
    }

    private record EmploymentSeed(
            String phone,
            LocalDate employedFrom,
            LocalDate employedTo,
            String position,
            String organizationName
    ) {
    }
}
