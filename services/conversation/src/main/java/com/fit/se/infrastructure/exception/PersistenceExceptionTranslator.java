package com.fit.se.infrastructure.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class PersistenceExceptionTranslator {

    public RuntimeException translate(DataAccessException exception) {
        return new InfrastructureException("Persistence layer failure", exception);
    }
}
