package com.fit.se.infrastructure.config;

import com.fit.se.application.common.port.ClockPort;
import com.fit.se.application.common.port.IdGeneratorPort;
import com.fit.se.application.common.port.TransactionPort;
import com.fit.se.application.port.output.qr.GenerateQrCodePort;
import com.fit.se.infrastructure.qr.QrCodeGeneratorAdapter;
import com.fit.se.infrastructure.support.clock.SystemClockAdapter;
import com.fit.se.infrastructure.support.id.UuidGeneratorAdapter;
import com.fit.se.infrastructure.support.transaction.SpringTransactionPortAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class InfrastructureBeanConfig {

    @Bean
    public ClockPort clockPort() {
        return new SystemClockAdapter();
    }

    @Bean
    public IdGeneratorPort idGeneratorPort() {
        return new UuidGeneratorAdapter();
    }

    @Bean
    public TransactionPort transactionPort(TransactionTemplate transactionTemplate) {
        return new SpringTransactionPortAdapter(transactionTemplate);
    }

    @Bean
    public GenerateQrCodePort generateQrCodePort() {
        return new QrCodeGeneratorAdapter();
    }
}
