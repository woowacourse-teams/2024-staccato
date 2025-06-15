package com.staccato;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import com.staccato.util.DatabaseCleanerExtension;

@ExtendWith(DatabaseCleanerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({ServiceTestConfig.class})
public abstract class ServiceSliceTest {
}
