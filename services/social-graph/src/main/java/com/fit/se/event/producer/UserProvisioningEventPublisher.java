package com.fit.se.event.producer;

public interface UserProvisioningEventPublisher {
    void publishProvisioned(String userId, String phone);
}