package com.staccato.flyway;

import java.util.Map;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import com.staccato.ContainerBaseTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(properties = {
        "spring.config.location=classpath:application-test.yml",
        "spring.flyway.enabled=false" // Flyway 자동 실행 방지
})
@ActiveProfiles("test")
public class FlywayTest extends ContainerBaseTest {
    public static final String FLYWAY_SCRIPT_PATH = "classpath:db/migration-test";

    @Autowired
    DataSource dataSource;

    @BeforeEach
    void setUp() {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(FLYWAY_SCRIPT_PATH)
                .cleanDisabled(false)
                .load();
        flyway.clean();
    }

    @Test
    void contextLoads_whenFlywayMigrationsApplied() {
        // given
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(FLYWAY_SCRIPT_PATH)
                .load();

        // when & then
        assertThatNoException().isThrownBy(flyway::migrate);
    }
}
