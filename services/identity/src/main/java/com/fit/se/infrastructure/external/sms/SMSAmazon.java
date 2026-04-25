package com.fit.se.infrastructure.external.sms;

import io.awspring.cloud.sns.sms.SmsMessageAttributes;
import io.awspring.cloud.sns.sms.SmsType;
import io.awspring.cloud.sns.sms.SnsSmsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SMSAmazon {
    private final SnsSmsTemplate template;

    public void send(String target, String message) {
        SmsMessageAttributes attributes = SmsMessageAttributes.builder()
                .smsType(SmsType.TRANSACTIONAL)
                .build();
        target = "+84" + target;
        try {
            template.send(target, message, attributes);
            System.out.println("SMS sent to: " + target);
        } catch (Exception e) {
            System.err.println("Failed to send SMS to: " + target);
            e.printStackTrace();
            throw e;
        }
    }
}
