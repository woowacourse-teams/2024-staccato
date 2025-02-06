package com.staccato.config.db;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import static com.staccato.config.db.ReaderDataSourceConfig.READER;
import static com.staccato.config.db.ReaderDataSourceConfig.READER_DATA_SOURCE;
import static com.staccato.config.db.WriterDataSourceConfig.WRITER;
import static com.staccato.config.db.WriterDataSourceConfig.WRITER_DATA_SOURCE;

/**
 * DB Replication을 위한 DataSource 설정 파일입니다.
 * 지금은 사용하지 않은 상태로 Disable 상태입니다.
 * <p>
 * 만약, 다시 사용하게 된다면 @Profile, @Configuration 설정이 필요합니다.
 */
public class DataSourceConfig {
    @DependsOn({WRITER_DATA_SOURCE, READER_DATA_SOURCE})
    @Bean
    public DataSource routingDataSource(
            @Qualifier(WRITER_DATA_SOURCE) DataSource writer,
            @Qualifier(READER_DATA_SOURCE) DataSource reader
    ) {
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(WRITER, writer);
        dataSourceMap.put(READER, reader);
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writer);
        return routingDataSource;
    }

    @Primary
    @DependsOn({"routingDataSource"})
    @Bean
    public DataSource dataSource(DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }
}
