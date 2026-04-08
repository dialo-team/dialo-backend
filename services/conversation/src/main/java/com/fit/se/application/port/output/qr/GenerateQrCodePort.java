package com.fit.se.application.port.output.qr;

public interface GenerateQrCodePort {
    byte[] generatePng(String content);
}
