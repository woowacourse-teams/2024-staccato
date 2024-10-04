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
public class ReaderDataSourceConfig {
    protected static final String READER_DATA_SOURCE = "readerDataSource";
    protected static final String READER = "reader";

    @ConfigurationProperties(prefix = "spring.datasource.reader")
    @Bean(name = READER_DATA_SOURCE)
    public DataSource readerDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
