package com.yourcompany.conversationservice.application.common.port;

public interface TransactionPort {
    <T> T executeInTransaction(TransactionCallback<T> callback);

    @FunctionalInterface
    interface TransactionCallback<T> {
        T execute();
    }
}
