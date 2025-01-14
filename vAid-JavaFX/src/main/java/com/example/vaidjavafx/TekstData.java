package com.example.vaidjavafx;

public class TekstData {
    private final int tekstId;
    private final String tekst;
    private final String tijdstip;
    private final String user;

    public TekstData(int tekstId, String tekst, String tijdstip, String user) {
        this.tekstId = tekstId;
        this.tekst = tekst;
        this.tijdstip = tijdstip;
        this.user = user;
    }

    public int getTekstId() {
        return tekstId;
    }

    public String getTekst() {
        return tekst;
    }

    public String getTijdstip() {
        return tijdstip;
    }

    public String getUser() {
        return user;
    }
}