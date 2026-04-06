package com.fit.se.infrastructure.qr;

import com.fit.se.application.port.output.qr.GenerateQrCodePort;

public class QrCodeGeneratorAdapter implements GenerateQrCodePort {

    private final QrCodeImageWriter writer = new QrCodeImageWriter();
    private final QrCodeProperties properties = new QrCodeProperties();

    @Override
    public byte[] generatePng(String content) {
        return writer.writePng(content, properties.getWidth(), properties.getHeight());
    }
}
