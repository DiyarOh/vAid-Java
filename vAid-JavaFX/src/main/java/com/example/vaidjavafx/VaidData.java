package com.example.vaidjavafx;

public class VaidData {
    private final int serienummer;
    private final String model;
    private final int softwareVersion;

    public VaidData(int serienummer, String model, int softwareVersion) {
        this.serienummer = serienummer;
        this.model = model;
        this.softwareVersion = softwareVersion;
    }

    public int getSerienummer() {
        return serienummer;
    }

    public String getModel() {
        return model;
    }

    public int getSoftwareVersion() {
        return softwareVersion;
    }
}