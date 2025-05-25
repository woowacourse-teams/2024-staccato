package com.staccato.flyway;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import com.staccato.ContainerBaseTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@ActiveProfiles("test")
public class FlywayTest extends ContainerBaseTest {

    @Test
    void contextLoads_whenFlywayMigrationsApplied() {
        // ✅ 아무 것도 하지 않아도 됨
        // 이 테스트가 통과하면:
        // - Flyway 마이그레이션이 모두 성공적으로 적용됨
        // - 컨텍스트 로딩에 실패하지 않음
        // - 따라서 마이그레이션에 문제가 없음을 보장
    }
}
