package com.staccato.config.db;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import com.zaxxer.hikari.HikariDataSource;

/**
 * DB Replication을 위한 DataSource 설정 파일입니다.
 * 지금은 사용하지 않은 상태로 Disable 상태입니다.
 * <p>
 * 만약, 다시 사용하게 된다면 @Profile, @Configuration 설정이 필요합니다.
 */
public class ReaderDataSourceConfig {
    protected static final String READER_DATA_SOURCE = "readerDataSource";
    protected static final String READER = "reader";

    @ConfigurationProperties(prefix = "spring.datasource.reader")
    @Bean(name = READER_DATA_SOURCE)
    public DataSource readerDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
