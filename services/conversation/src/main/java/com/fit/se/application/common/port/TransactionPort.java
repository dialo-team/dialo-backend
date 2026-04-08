package com.fit.se.application.common.port;

public interface TransactionPort {
    <T> T executeInTransaction(TransactionCallback<T> callback);

    interface TransactionCallback<T> {
        T doInTransaction();
    }
}
