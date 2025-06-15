package com.staccato;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@Import({FcmTestConfig.class})
public abstract class ContainerBaseTest {

    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("staccato")
            .withUsername("root")
            .withPassword("1234");

    static {
        mysqlContainer.start();
    }

    @DynamicPropertySource
    private static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.driver-class-name", mysqlContainer::getDriverClassName);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);

        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.flyway.baseline-on-migrate", () -> true);
        registry.add("spring.flyway.locations", () -> "classpath:db/migration-test");
        registry.add("spring.flyway.connect-retries", () -> "30");
    }
}
