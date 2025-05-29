package com.staccato.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class TransactionExecutor {

    @Autowired
    private PlatformTransactionManager transactionManager;

    public void executeInNewTransaction(Runnable task) {
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setPropagationBehavior(Propagation.REQUIRES_NEW.value());
        txTemplate.executeWithoutResult(status -> task.run());
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
