package ru.job4j.devops.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

class TestContainersIT {
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        POSTGRES.start();
    }

    @AfterAll
    static void afterAll() {
        POSTGRES.stop();
    }

    @Test
    public void whenSaveUser() {
        System.out.println(POSTGRES.getJdbcUrl());
        System.out.println(POSTGRES.getUsername());
        System.out.println(POSTGRES.getPassword());
    }
}