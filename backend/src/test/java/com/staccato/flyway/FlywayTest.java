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

    @DisplayName("V8__create_staccato_auditor 마이그레이션 성공 테스트: staccato가 속한 category host의 논리적 삭제 여부와 상관 없이 created_by/modified_by로 세팅한다.")
    @Test
    void shouldAddAuditorFieldsAndSetHostAsDefault() throws Exception {
        // given
        Flyway.configure()
                .dataSource(dataSource)
                .locations(FLYWAY_SCRIPT_PATH)
                .target("7")
                .load()
                .migrate();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.update("INSERT INTO member (id, nickname, code, is_deleted) VALUES (?, ?, ?, ?)",
                1L, "host", java.util.UUID.randomUUID().toString(), true);
        jdbcTemplate.update("INSERT INTO member (id, nickname, code, is_deleted) VALUES (?, ?, ?, ?)",
                2L, "guest", java.util.UUID.randomUUID().toString(), false);
        jdbcTemplate.update("INSERT INTO category (id, title, is_shared) VALUES (?, ?, ?)",
                1L, "테스트", false);
        jdbcTemplate.update("INSERT INTO category_member (category_id, member_id, role) VALUES (?, ?, 'HOST')",
                1L, 1L);
        jdbcTemplate.update("INSERT INTO category_member (category_id, member_id, role) VALUES (?, ?, 'GUEST')",
                1L, 2L);
        jdbcTemplate.update("""
            INSERT INTO staccato (id, visited_at, category_id, title, place_name, address, latitude, longitude, feeling)
            VALUES (?, NOW(), ?, '제목', '장소', '주소', 0.0, 0.0, 'NOTHING')
        """, 1L, 1L);

        // when
        Flyway.configure()
                .dataSource(dataSource)
                .locations(FLYWAY_SCRIPT_PATH)
                .load()
                .migrate();

        // then
        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT created_by, modified_by FROM staccato WHERE id = ?", 1L
        );

        assertAll(
                () -> assertThat(result.get("created_by")).isEqualTo(1L),
                () -> assertThat(result.get("modified_by")).isEqualTo(1L)
        );
    }
}
