package com.yourcompany.conversationservice.application.port.output.qr;

public interface GenerateQrCodePort {
    byte[] generatePng(String text, int width, int height);
}
