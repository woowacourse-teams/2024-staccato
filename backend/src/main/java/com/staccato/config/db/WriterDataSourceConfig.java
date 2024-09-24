package com.staccato.config.db;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@Profile("prod")
public class WriterDataSourceConfig {
    protected static final String WRITER_DATA_SOURCE = "writerDataSource";
    protected static final String WRITER = "writer";

    @ConfigurationProperties(prefix = "spring.datasource.writer")
    @Bean(name = WRITER_DATA_SOURCE)
    public DataSource writerDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
