package com.axi.loan.seed;

import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@Order(1)
public class ClientSeeder implements Seeder {

    private static final String INSERT_CLIENT = """
            INSERT INTO clients (
                first_name, last_name, middle_name, phone,
                residential_address, registration_address,
                gender, marital_status, created_at, updated_at
            )
            VALUES (?, ?, ?, ?, ?, ?, CAST(? AS gender_type),
                    CAST(? AS marital_status_type), ?, ?)
            ON CONFLICT (phone) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    public ClientSeeder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void seed() {
        OffsetDateTime createdAt = OffsetDateTime.of(
                2026, 1, 15, 10, 0, 0, 0, ZoneOffset.UTC);

        List<Object[]> clients = List.of(
                new Object[]{"Иван", "Петров", "Сергеевич", "+79001234567",
                        "Москва, ул. Тверская, д. 10", "Москва, ул. Тверская, д. 10",
                        "MALE", "MARRIED", createdAt, createdAt},
                new Object[]{"Анна", "Соколова", "Викторовна", "+79007654321",
                        "Санкт-Петербург, Невский проспект, д. 25", "Тула, ул. Ленина, д. 8",
                        "FEMALE", "SINGLE", createdAt.plusDays(1), createdAt.plusDays(1)},
                new Object[]{"Михаил", "Волков", "Андреевич", "+79005554433",
                        "Казань, ул. Баумана, д. 17", "Казань, ул. Баумана, д. 17",
                        "MALE", "DIVORCED", createdAt.plusDays(2), createdAt.plusDays(2)},
                new Object[]{"Елена", "Морозова", "Игоревна", "+79001112233",
                        "Новосибирск, Красный проспект, д. 42", "Новосибирск, Красный проспект, д. 42",
                        "FEMALE", "MARRIED", createdAt.plusDays(3), createdAt.plusDays(3)},
                new Object[]{"Дмитрий", "Кузнецов", "Олегович", "+79002223344",
                        "Екатеринбург, ул. Малышева, д. 31", "Пермь, ул. Ленина, д. 19",
                        "MALE", "SINGLE", createdAt.plusDays(4), createdAt.plusDays(4)},
                new Object[]{"Ольга", "Лебедева", "Александровна", "+79003334455",
                        "Самара, Московское шоссе, д. 15", "Самара, ул. Гагарина, д. 28",
                        "FEMALE", "DIVORCED", createdAt.plusDays(5), createdAt.plusDays(5)},
                new Object[]{"Сергей", "Новиков", "Павлович", "+79004445566",
                        "Омск, проспект Мира, д. 7", "Омск, проспект Мира, д. 7",
                        "MALE", "MARRIED", createdAt.plusDays(6), createdAt.plusDays(6)},
                new Object[]{"Наталья", "Фёдорова", "Романовна", "+79006667788",
                        "Уфа, проспект Октября, д. 63", "Уфа, проспект Октября, д. 63",
                        "FEMALE", "SINGLE", createdAt.plusDays(7), createdAt.plusDays(7)},
                new Object[]{"Алексей", "Павлов", "Денисович", "+79008889900",
                        "Ростов-на-Дону, ул. Садовая, д. 54", "Воронеж, проспект Революции, д. 11",
                        "MALE", "MARRIED", createdAt.plusDays(8), createdAt.plusDays(8)},
                new Object[]{"Мария", "Орлова", "Константиновна", "+79009990011",
                        "Краснодар, ул. Северная, д. 88", "Краснодар, ул. Северная, д. 88",
                        "FEMALE", "SINGLE", createdAt.plusDays(9), createdAt.plusDays(9)}
        );

        jdbcTemplate.batchUpdate(INSERT_CLIENT, clients);
    }
}
