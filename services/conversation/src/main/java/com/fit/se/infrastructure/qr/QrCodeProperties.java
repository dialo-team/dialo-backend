package com.fit.se.infrastructure.qr;

public class QrCodeProperties {

    private int width = 256;
    private int height = 256;
    private String format = "PNG";

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}
