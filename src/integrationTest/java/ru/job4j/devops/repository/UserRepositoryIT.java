package ru.job4j.devops.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.job4j.devops.models.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserRepositoryIT {
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    ).withReuse(true);

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        /* Force Liquibase to find the changelog in the main resources */
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/db.changelog-master.xml");
        registry.add("spring.liquibase.enabled", () -> "true");

        /* Tell Hibernate to only validate the schema, as Liquibase should create it */
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    public void whenSaveUser() {
        var user = new User();
        user.setName("Job4j");
        userRepository.save(user);
        var foundUser = userRepository.findById(user.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Job4j");
    }
}
