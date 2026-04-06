package com.fit.se.infrastructure.support.transaction;

import com.fit.se.application.common.port.TransactionPort;
import org.springframework.transaction.support.TransactionTemplate;

public class SpringTransactionPortAdapter implements TransactionPort {

    private final TransactionTemplate transactionTemplate;

    public SpringTransactionPortAdapter(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public <T> T executeInTransaction(TransactionCallback<T> callback) {
        return transactionTemplate.execute(status -> callback.doInTransaction());
    }
}
