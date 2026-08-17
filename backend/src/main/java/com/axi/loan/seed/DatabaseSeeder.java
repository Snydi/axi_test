package com.axi.loan.seed;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DatabaseSeeder implements ApplicationRunner {

    private final List<Seeder> seeders;

    public DatabaseSeeder(List<Seeder> seeders) {
        this.seeders = seeders;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments arguments) {
        seeders.forEach(Seeder::seed);
    }
}
