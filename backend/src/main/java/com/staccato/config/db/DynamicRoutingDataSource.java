package com.staccato.config.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.transaction.support.TransactionSynchronizationManager.isCurrentTransactionReadOnly;
import static com.staccato.config.db.ReaderDataSourceConfig.READER;
import static com.staccato.config.db.WriterDataSourceConfig.WRITER;

/**
 * DB Replication을 위한 DataSource 설정 파일입니다.
 * 지금은 사용하지 않은 상태로 Disable 상태입니다.
 * <p>
 */
@Slf4j
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        if (isCurrentTransactionReadOnly()) {
            return READER;
        }
        return WRITER;
    }
}
