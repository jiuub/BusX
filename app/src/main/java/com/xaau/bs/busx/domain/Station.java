package com.xaau.bs.busx.domain;

public class Station {
    private int sta_No;
    private String sta_Name;

    public Station() {

    }

    public Station(int sta_No, String sta_Name) {
        this.sta_No = sta_No;
        this.sta_Name = sta_Name;
    }

    public int getSta_No() {
        return sta_No;
    }

    public void setSta_No(int sta_No) {
        this.sta_No = sta_No;
    }

    public String getSta_Name() {
        return sta_Name;
    }

    public void setSta_Name(String sta_Name) {
        this.sta_Name = sta_Name;
    }

    @Override
    public String toString() {
        return "Station{" +
                "sta_No=" + sta_No +
                ", sta_Name='" + sta_Name + '\'' +
                '}';
    }
}
